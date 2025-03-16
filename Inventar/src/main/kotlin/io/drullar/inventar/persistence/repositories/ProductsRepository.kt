package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.persistence.schema.Products
import io.drullar.inventar.persistence.schema.Products.uid
import io.drullar.inventar.persistence.schema.Products.availableQuantity
import io.drullar.inventar.persistence.schema.Products.barcode
import io.drullar.inventar.persistence.schema.Products.name
import io.drullar.inventar.persistence.schema.Products.providerPrice
import io.drullar.inventar.persistence.schema.Products.sellingPrice
import io.drullar.inventar.result
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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
            }.resultedValues?.first()?.let { queryResult ->
                transformResultRowToModel(queryResult)
            }
                ?: throw DatabaseException.PersistenceException("Could not save product with name ${dto.name}")
        }
    }

    override fun update(
        id: Int,
        model: ProductCreationDTO
    ): Result<ProductDTO> {
        withTransaction {
            table.update(where = { table.uid.eq(id) }) {
                it[name] = model.name
                it[availableQuantity] = model.availableQuantity
                it[sellingPrice] = model.sellingPrice
                it[barcode] = model.barcode
                model.providerPrice?.let { price -> it[providerPrice] = price }
            }
        }
        return getById(id)
    }

    override fun getById(id: Int) = result {
        withTransaction {
            table.selectAll().where { table.uid.eq(id) }.firstOrNull()
                ?.let { transformResultRowToModel(it) }
        }
            ?: throw DatabaseException.NoSuchElementFoundException("Couldn't find product with id $id")
    }

    override fun deleteById(id: Int) = result {
        withTransaction {
            table.deleteWhere { table.uid.eq(id) }
        }
        return@result Unit
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

    enum class SortBy {
        NAME,
        ID,
        AVAILABLE_QUANTITY,
        PROVIDER_PRICE,
        SELLING_PRICE
    }
}