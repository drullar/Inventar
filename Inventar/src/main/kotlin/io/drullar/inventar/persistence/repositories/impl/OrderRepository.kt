package io.drullar.inventar.persistence.repositories.impl

import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.persistence.repositories.AbstractRepository
import io.drullar.inventar.persistence.schema.Orders
import io.drullar.inventar.persistence.schema.Orders.creationDate
import io.drullar.inventar.persistence.schema.Orders.id
import io.drullar.inventar.persistence.schema.Orders.orderStatus
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.orderUid
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.orderedAmount
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.productUid
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.sellingPrice
import io.drullar.inventar.result
import io.drullar.inventar.shared.ISortBy
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.ProductSoldAmountDTO
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.sortedBy
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object OrderRepository :
    AbstractRepository<Orders, OrderDTO, OrderCreationDTO, Int, OrderRepository.OrderSortBy>(Orders) {
    private val productOrderAssociationTable = ProductOrderAssociation

    override fun save(dto: OrderCreationDTO): Result<OrderDTO> = result {
        withTransaction {
            val orderId =
                table.insert {
                    it[orderStatus] = dto.status
                    it[creationDate] = dto.creationDate
                }.resultedValues?.first()?.let {
                    it[id]
                } ?: throw DatabaseException.PersistenceException("Couldn't save the order")

            if (dto.productToQuantity.isNotEmpty()) {
                associateOrderWithProduct(orderId, dto.productToQuantity)

                if (dto.status == OrderStatus.COMPLETED) {
                    performPostOrderCompletionOperations(orderId)
                }
            } else {
                if (dto.status == OrderStatus.COMPLETED) throw DatabaseException.InvalidOperationException(
                    "Can not save an order without selected products."
                )
            }

            return@withTransaction getById(orderId).getOrThrow()
        }
    }

    override fun deleteById(id: Int): Result<Unit> = result {
        val order = getById(id).getOrNull()
        if (order != null && order.status == OrderStatus.COMPLETED)
            throw DatabaseException.InvalidOperationException("Can not delete an order which has already been completed.")

        withTransaction {
            table.deleteWhere { table.id.eq(id) }
        }
    }

    override fun getById(id: Int): Result<OrderDTO> = result {
        withTransaction {
            table.selectAll().where { table.id.eq(id) }.first().let {
                transformResultRowToModel(it)
            }
        }
    }

    override fun update(id: Int, dto: OrderCreationDTO): Result<OrderDTO> = result {
        val preUpdateOrder = getById(id).getOrNull()
            ?: throw DatabaseException.NoSuchElementFoundException("No order with id $id found.")

        if (preUpdateOrder.status == OrderStatus.COMPLETED)
            throw DatabaseException.InvalidOperationException("Can not update order which has already been completed")

        withTransaction {
            table.update(where = { table.id.eq(id) }) {
                it[orderStatus] = dto.status
            }
        }

        updateOrderProductAssociations(id, preUpdateOrder.productToQuantity, dto.productToQuantity)
        if (dto.status == OrderStatus.COMPLETED) {
            performPostOrderCompletionOperations(id)
        }

        return@result getById(id).getOrThrow()
    }

    override fun transformResultRowToModel(row: ResultRow): OrderDTO = OrderDTO(
        orderId = row[id],
        status = row[orderStatus],
        productToQuantity = getProductToSoldAmountAssociation(row[id]).toMutableMap(),
        creationDate = row[creationDate]
    )

    fun getAllByStatus(status: OrderStatus): Result<List<OrderDTO>> = result {
        withTransaction {
            table.selectAll().where { table.orderStatus.eq(status) }
                .orderBy(table.creationDate)
                .map { transformResultRowToModel(it) }
        }
    }

    fun getAllProductsAssociatedWithOrder(orderId: Int): Result<List<ProductDTO>> =
        result {
            withTransaction {
                productOrderAssociationTable.selectAll()
                    .where { productOrderAssociationTable.orderUid.eq(orderId) }.map {
                        ProductsRepository.getById(it[productUid]).getOrNull()!!
                    }
            }
        }

    fun getCountByStatus(status: OrderStatus) = withTransaction {
        table.selectAll().where { table.orderStatus.eq(status) }.count()
    }

    /**
     * Get the total sold amount of [productId] for the given [fromDate] and [untilDate].
     * If [fromDate] is null Epoch 0 will be used.
     * If [untilDate] is null the current timestamp will be used
     */
    fun getProductSoldAmount(productId: Int, fromDate: LocalDate?, untilDate: LocalDate?) = result {
        withTransaction {
            val predicateFromDate =
                fromDate?.atStartOfDay() ?: LocalDateTime.ofInstant(
                    Instant.EPOCH,
                    ZoneId.systemDefault()
                )
            val predicateUntilDate =
                untilDate?.let { LocalDateTime.of(it, LocalTime.now()) } ?: LocalDateTime.now()
            val result = productOrderAssociationTable.innerJoin(table).selectAll().where {

                productOrderAssociationTable.productUid.eq(productId)
                    .and(table.creationDate.between(predicateFromDate, predicateUntilDate))
                    .and(table.orderStatus.eq(OrderStatus.COMPLETED))
            }

            val soldAmount = result.sumOf { it[orderedAmount] }
            ProductSoldAmountDTO(
                productId,
                soldAmount,
                predicateFromDate.toLocalDate(),
                predicateUntilDate.toLocalDate()
            )
        }
    }

    /**
     * Gets products based on their sales between [fromDate] and [untilDate].
     * The resulting list contains [limit] amount of products or less, based on sales and products that are saved.
     * If [fromDate] is null Epoch 0 will be used.
     * If [untilDate] is null the current timestamp will be used
     */
    fun getMostSoldProducts(limit: Int, fromDate: LocalDate?, untilDate: LocalDate?) = result {
        withTransaction {
            val predicateFromDate =
                fromDate?.atStartOfDay() ?: LocalDateTime.ofInstant(
                    Instant.EPOCH,
                    ZoneId.systemDefault()
                )
            val predicateUntilDate =
                untilDate?.let { LocalDateTime.of(it, LocalTime.now()) } ?: LocalDateTime.now()
            val result = productOrderAssociationTable.innerJoin(table).selectAll().where {
                (table.creationDate.between(predicateFromDate, predicateUntilDate))
                    .and(table.orderStatus.eq(OrderStatus.COMPLETED))
            }.groupBy { it[productUid] }.map { entry ->
                val productId = entry.key
                val sales = entry.value.sumOf { it[orderedAmount] }
                ProductSoldAmountDTO(
                    productId = productId,
                    soldQuantity = sales,
                    fromDate = predicateFromDate.toLocalDate(),
                    untilDate = predicateFromDate.toLocalDate()
                )
            }

            result.sortedBy(SortingOrder.DESCENDING) { it.soldQuantity }.subList(0, limit)
        }
    }

    override fun buildOrderByExpression(sortBy: OrderSortBy): Expression<*> = when (sortBy) {
        OrderSortBy.NUMBER -> {
            table.id
        }

        OrderSortBy.CREATION_DATE -> {
            table.creationDate
        }

        OrderSortBy.STATUS -> {
            table.orderStatus
        }
    }

    private fun associateOrderWithProduct(
        orderId: Int,
        productToQuantity: Map<ProductDTO, Int>
    ) =
        productOrderAssociationTable.batchInsert(productToQuantity.keys) {
            val product = it
            val quantity = productToQuantity[product]!!
            this[orderUid] = orderId
            this[orderedAmount] = quantity
            this[sellingPrice] = product.sellingPrice
            this[productUid] = product.uid
        }.filter { ProductsRepository.getById(it[productUid]).getOrNull() != null }
            .associate {
                ProductsRepository.getById(it[productUid])
                    .getOrNull()!! to it[orderedAmount]
            }

    private fun disassociateOrderWithProducts(orderId: Int, products: Set<ProductDTO>) {
        productOrderAssociationTable.deleteWhere {
            productOrderAssociationTable.orderUid.eq(orderId)
                .and(productOrderAssociationTable.productUid.inList(products.map { it.uid }))
        }
    }

    private fun performPostOrderCompletionOperations(id: Int) {
        val order = getById(id).getOrNull()!!
        updateProductsQuantityOnCompletion(order.productToQuantity)
    }

    private fun updateOrderProductAssociations(
        orderId: Int,
        oldProducts: Map<ProductDTO, Int>,
        requiredProducts: Map<ProductDTO, Int>
    ) {
        withTransaction {
            val removedProducts = oldProducts.minus(requiredProducts.keys)
            if (removedProducts.isNotEmpty()) {
                disassociateOrderWithProducts(orderId, removedProducts.keys)
            }

            val productsWithUpdatedQuantityValues = requiredProducts.filter { (product, quantity) ->
                product in oldProducts && quantity != oldProducts[product]
            }

            productsWithUpdatedQuantityValues.forEach { (product, quantity) ->
                productOrderAssociationTable.update(
                    where = {
                        productOrderAssociationTable.orderUid.eq(orderId).and(
                            productUid.eq(product.uid)
                        )
                    }
                ) {
                    it[orderedAmount] = quantity
                }
            }

            val newProducts = requiredProducts.minus(oldProducts.keys)
            if (newProducts.isNotEmpty()) {
                associateOrderWithProduct(orderId, newProducts)
            }
        }
    }

    private fun getProductToSoldAmountAssociation(orderId: Int): Map<ProductDTO, Int> =
        withTransaction {
            productOrderAssociationTable.selectAll()
                .where { productOrderAssociationTable.orderUid.eq(orderId) }.associate {
                    val product = ProductsRepository.getByIdRegardlessOfDeletionMark(it[productUid])
                        .getOrThrow()

                    product.copy(sellingPrice = it[sellingPrice]) to it[orderedAmount]
                }
        }

    private fun updateProductsQuantityOnCompletion(productsToSoldQuantity: Map<ProductDTO, Int>) {
        productsToSoldQuantity.keys.forEach { product ->
            val soldQuantity = productsToSoldQuantity[product]!!
            val updatedAvailability = (product.availableQuantity - soldQuantity).coerceAtLeast(0)
            ProductsRepository.update(
                product.uid,
                product.copy(availableQuantity = updatedAvailability).toProductCreationDTO()
            )
        }
    }

    enum class OrderSortBy(val text: String) : ISortBy {
        NUMBER("field.number"),
        CREATION_DATE("field.date"),
        STATUS("field.status"),
        // TOTAL_SUM TODO figure it out. SQL join?
    }
}