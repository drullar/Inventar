package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.schema.Products
import io.drullar.inventar.persistence.schema.Products.uid
import io.drullar.inventar.persistence.schema.Products.availableQuantity
import io.drullar.inventar.persistence.schema.Products.barcode
import io.drullar.inventar.persistence.schema.Products.name
import io.drullar.inventar.persistence.schema.Products.providerPrice
import io.drullar.inventar.persistence.schema.Products.sellingPrice
import io.drullar.inventar.shared.ProductDTO
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal object ProductsService :
    AbstractPersistenceRepository<Products, ProductDTO, Int>(table = Products) {

    override fun save(model: ProductDTO): Int = withTransaction {
        val result = table.insert {
            it[name] = model.name
            it[availableQuantity] = model.availableQuantity
            it[sellingPrice] = model.sellingPrice
            it[barcode] = model.barcode
            it[providerPrice] = model.providerPrice
        }.resultedValues?.first()
        result?.let { it[uid] }
            ?: throw Exception("Could not save product with name ${model.name}")
    }

    override fun update(id: Int, model: ProductDTO) {
        withTransaction {
            table.update(where = { table.uid.eq(id) }) {
                it[name] = model.name
                it[availableQuantity] = model.availableQuantity
                it[sellingPrice] = model.sellingPrice
                model.providerPrice?.let { price -> it[providerPrice] = price }
            }
        }
    }

    override fun getById(id: Int): ProductDTO? = withTransaction {
        table.selectAll().where { table.uid.eq(id) }.firstOrNull()?.let {
            transformResultRowToModel(it)
        }
    }

    override fun deleteById(id: Int) {
        withTransaction {
            table.deleteWhere { table.uid.eq(id) }
        }
    }

    override fun transformResultRowToModel(row: ResultRow): ProductDTO = ProductDTO(
        uid = row[uid],
        name = row[name],
        availableQuantity = row[availableQuantity],
        providerPrice = row[providerPrice],
        sellingPrice = row[sellingPrice],
        barcode = row[barcode]
    )
}