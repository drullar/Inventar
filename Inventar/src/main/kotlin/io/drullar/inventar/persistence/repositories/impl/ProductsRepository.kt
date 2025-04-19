package io.drullar.inventar.persistence.repositories.impl

import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.persistence.repositories.AbstractRepository
import io.drullar.inventar.persistence.schema.Orders
import io.drullar.inventar.persistence.schema.Products
import io.drullar.inventar.persistence.schema.Products.uid
import io.drullar.inventar.persistence.schema.Products.availableQuantity
import io.drullar.inventar.persistence.schema.Products.barcode
import io.drullar.inventar.persistence.schema.Products.name
import io.drullar.inventar.persistence.schema.Products.providerPrice
import io.drullar.inventar.persistence.schema.Products.sellingPrice
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation
import io.drullar.inventar.persistence.schema.associative.ProductOrderAssociation.productUid
import io.drullar.inventar.result
import io.drullar.inventar.shared.ISortBy
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList

object ProductsRepository :
    AbstractRepository<Products, ProductDTO, ProductCreationDTO, Int, ProductsRepository.SortBy>(
        Products
    ) {

    override fun save(dto: ProductCreationDTO) = result {
        withTransaction {
            table.insert {
                it[name] = dto.name
                it[availableQuantity] = dto.availableQuantity
                it[sellingPrice] = dto.sellingPrice
                it[barcode] = dto.barcode
                it[providerPrice] = dto.providerPrice
                it[isMarkedForDeletion] = dto.isMarkedForDeletion
            }.resultedValues?.first()?.let { queryResult ->
                transformResultRowToModel(queryResult)
            }
                ?: throw DatabaseException.PersistenceException("Could not save product with name ${dto.name}")
        }
    }

    override fun update(
        id: Int,
        dto: ProductCreationDTO
    ): Result<ProductDTO> {
        val beforeUpdate = getById(id).getOrThrow()
        withTransaction {
            table.update(where = { table.uid.eq(id) }) {
                it[name] = dto.name
                it[availableQuantity] = dto.availableQuantity
                it[sellingPrice] = dto.sellingPrice
                it[barcode] = dto.barcode
                it[isMarkedForDeletion] = dto.isMarkedForDeletion
                dto.providerPrice?.let { price -> it[providerPrice] = price }
            }

            // Update selling price in all unfinished orders
            if (beforeUpdate.sellingPrice != dto.sellingPrice) {
                ProductOrderAssociation.update(
                    where = {
                        productUid eq id and (
                                ProductOrderAssociation.orderUid inSubQuery (Orders.select(Orders.id)
                                    .where { Orders.orderStatus eq OrderStatus.DRAFT })
                                )
                    }
                ) {
                    it[sellingPrice] = dto.sellingPrice
                }
            }
        }
        return getById(id)
    }

    /**
     * Returns the product if it exists and if it is not marked for deletion
     */
    override fun getById(id: Int) = result {
        withTransaction {
            table.selectAll().where { table.uid.eq(id).and(table.isMarkedForDeletion.eq(false)) }
                .firstOrNull()
                ?.let { transformResultRowToModel(it) }
        }
            ?: throw DatabaseException.NoSuchElementFoundException("Couldn't find product with id $id")
    }

    /**
     * Returns the product regardless of whether it's marked for deletion or not
     */
    fun getByIdRegardlessOfDeletionMark(id: Int) = result {
        withTransaction {
            table.selectAll().where { table.uid.eq(id) }.firstOrNull()
                ?.let { transformResultRowToModel(it) }
        }
            ?: throw DatabaseException.NoSuchElementFoundException("Couldn't find product with id $id")
    }

    override fun deleteById(id: Int) = result {
        withTransaction {
            val occurrencesInOrder = ProductOrderAssociation.selectAll().where {
                productUid.eq(id)
            }.count()

            if (occurrencesInOrder == 0L) {
                table.deleteWhere { table.uid.eq(id) }
            } else {
                markAsDeleted(listOf(id))
            }
        }
        return@result
    }

    override fun search(
        searchQuery: String,
        pageRequest: PagedRequest<SortBy>
    ): Result<Page<ProductDTO>> = result {
        val shouldSearchById = searchQuery.toIntOrNull() != null
        var count: Long = 0
        val items = withTransaction {
            val searchPattern = "%$searchQuery%"
            table.selectAll().where {
                table.name.like(searchPattern).or {
                    table.barcode.like(searchPattern)
                }.let { predicate ->
                    if (shouldSearchById) predicate.or {
                        table.uid.eq(searchQuery.toInt())
                    }
                    else predicate
                }
            }.also {
                count = it.count()
            }.limit(
                pageRequest.pageSize,
                ((pageRequest.page - 1) * pageRequest.pageSize).toLong()
            ).map {
                transformResultRowToModel(it)
            }
        }
        Page(
            pageNumber = pageRequest.page,
            isLastPage = pageRequest.pageSize * pageRequest.page >= count,
            items = items,
            itemsPerPage = pageRequest.pageSize,
            totalItems = count
        )
    }

    override fun deleteAll(): Result<Unit> = result {
        withTransaction {
            val productsInOrder =
                table.innerJoin(ProductOrderAssociation)
                    .select(
                        listOf(
                            Column(
                                ProductOrderAssociation,
                                productUid.name,
                                productUid.columnType,
                            )
                        )
                    ).map { it[productUid] }
            markAsDeleted(productsInOrder)

            table.deleteWhere { table.uid.notInList(productsInOrder) }
        }

        return@result
    }

    override fun getCount(): Result<Long> = result {
        withTransaction {
            table.selectAll().where { table.isMarkedForDeletion.eq(false) }.count()
        }
    }

    override fun buildOrderByExpression(sortBy: SortBy): Expression<*> = when (sortBy) {
        SortBy.NAME -> {
            table.name
        }

        SortBy.ID -> {
            table.uid
        }

        SortBy.AVAILABLE_QUANTITY -> {
            table.availableQuantity
        }

        SortBy.PROVIDER_PRICE -> {
            table.providerPrice
        }

        SortBy.SELLING_PRICE -> {
            table.sellingPrice
        }
    }

    override fun transformResultRowToModel(row: ResultRow): ProductDTO =
        ProductDTO(
            uid = row[uid],
            name = row[name],
            availableQuantity = row[availableQuantity],
            providerPrice = row[providerPrice],
            sellingPrice = row[sellingPrice],
            barcode = row[barcode]
        )

    private fun markAsDeleted(ids: List<Int>) {
        table.update(where = { table.uid.inList(ids) }) {
            it[isMarkedForDeletion] = true
        }
    }

    enum class SortBy : ISortBy {
        NAME,
        ID,
        AVAILABLE_QUANTITY,
        PROVIDER_PRICE,
        SELLING_PRICE
    }
}