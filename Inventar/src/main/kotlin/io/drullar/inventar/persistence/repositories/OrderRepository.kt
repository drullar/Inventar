package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.persistence.schema.Orders
import io.drullar.inventar.persistence.schema.Orders.creationDate
import io.drullar.inventar.persistence.schema.Orders.id
import io.drullar.inventar.persistence.schema.Orders.orderStatus
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.orderUid
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.orderedAmount
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.productUid
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.sellingPrice
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.RepositoryResponse
import io.drullar.inventar.shared.getDataOnSuccessOrNull
import io.drullar.inventar.shared.response
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList

object OrderRepository : AbstractRepository<Orders, OrderDTO, OrderCreationDTO, Int>(Orders) {
    private val productRepository = ProductsRepository
    private val productOrderAssociationTable = ProductOrderAssociation

    override fun save(dto: OrderCreationDTO): RepositoryResponse<OrderDTO> = response {
        withTransaction {
            val orderId =
                table.insert { it[orderStatus] = dto.status }.resultedValues?.first()?.let {
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

            return@withTransaction getById(orderId).getDataOnSuccessOrNull()!!
        }
    }

    override fun deleteById(id: Int): RepositoryResponse<Unit> = response {
        val order = getById(id).getDataOnSuccessOrNull()
        if (order != null && order.status == OrderStatus.COMPLETED)
            throw DatabaseException.InvalidOperationException("Can not delete an order which has already been completed.")

        withTransaction {
            table.deleteWhere { table.id.eq(id) }
        }
    }

    override fun getById(id: Int): RepositoryResponse<OrderDTO> = response {
        withTransaction {
            table.selectAll().where { table.id.eq(id) }.first().let {
                transformResultRowToModel(it)
            }
        }
    }

    override fun update(id: Int, dto: OrderCreationDTO): RepositoryResponse<OrderDTO> = response {
        val preUpdateOrder = getById(id).getDataOnSuccessOrNull()
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

        return@response getById(id).getDataOnSuccessOrNull()!!
    }

    override fun transformResultRowToModel(row: ResultRow): OrderDTO = OrderDTO(
        orderId = row[id],
        status = row[orderStatus],
        productToQuantity = getProductToSoldAmountAssociation(row[id]).toMutableMap(),
        creationDate = row[creationDate]
    )

    fun getAllByStatus(status: OrderStatus): RepositoryResponse<List<OrderDTO>> = response {
        withTransaction {
            table.selectAll().where { table.orderStatus.eq(status) }.orderBy(table.creationDate)
                .map { transformResultRowToModel(it) }
        }
    }

    fun getAllProductsAssociatedWithOrder(orderId: Int): RepositoryResponse<List<ProductDTO>> =
        response {
            withTransaction {
                productOrderAssociationTable.selectAll()
                    .where { productOrderAssociationTable.orderUid.eq(orderId) }.map {
                        productRepository.getById(it[productUid]).getDataOnSuccessOrNull()!!
                    }
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
        }.filter { productRepository.getById(it[productUid]).getDataOnSuccessOrNull() != null }
            .associate {
                productRepository.getById(it[productUid])
                    .getDataOnSuccessOrNull()!! to it[orderedAmount]
            }

    private fun disassociateOrderWithProducts(orderId: Int, products: Set<ProductDTO>) {
        productOrderAssociationTable.deleteWhere {
            productOrderAssociationTable.orderUid.eq(orderId)
                .and(productOrderAssociationTable.productUid.inList(products.map { it.uid }))
        }
    }

    private fun performPostOrderCompletionOperations(id: Int) {
        val order = getById(id).getDataOnSuccessOrNull()!!
        order.productToQuantity.forEach { (orderedProduct, orderedAmount) ->
            val product = productRepository.getById(orderedProduct.uid).getDataOnSuccessOrNull()!!
            if (product.availableQuantity - orderedAmount < 0) throw DatabaseException.InvalidOperationException(
                "Insufficient amount from ${product.name}. The available quantity from this product is: ${product.availableQuantity}"
            )
            productRepository.update(
                product.uid,
                product.copy(availableQuantity = product.availableQuantity - orderedAmount)
                    .toProductCreationDTO()
            )
        }
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
                    productRepository.getById(it[productUid])
                        .getDataOnSuccessOrNull()!! to it[orderedAmount]
                }
        }

    fun getCountByStatus(status: OrderStatus) = withTransaction {
        table.selectAll().where { table.orderStatus.eq(status) }.count()
    }
}