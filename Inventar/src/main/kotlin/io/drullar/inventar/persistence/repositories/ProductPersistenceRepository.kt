package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.schema.Products
import io.drullar.inventar.persistence.schema.Products.id
import io.drullar.inventar.persistence.schema.Products.inStockQuantity
import io.drullar.inventar.persistence.schema.Products.name
import io.drullar.inventar.persistence.schema.Products.providerPrice
import io.drullar.inventar.persistence.schema.Products.sellingPrice
import io.drullar.inventar.persistence.model.Product
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal object ProductPersistenceRepository :
    AbstractPersistenceRepository<Products, Product, Int>(table = Products) {
    override fun save(payload: Product): Int = withTransaction {
        val result = table.insert {
            it[name] = payload.name
            it[inStockQuantity] = payload.inStockQuantity
            payload.sellingPrice?.let { price -> it[sellingPrice] = price }
            payload.providerPrice?.let { price -> it[providerPrice] = price }
        }.resultedValues?.first()
        result?.let { it[id] } ?: throw Exception("Could not save product with name ${payload.name}")
    }

    override fun update(id: Int, payload: Product) {
        withTransaction {
            table.update(where = { table.id.eq(id) }) {
                it[name] = payload.name
                it[inStockQuantity] = payload.inStockQuantity
                payload.sellingPrice?.let { price -> it[sellingPrice] = price }
                payload.providerPrice?.let { price -> it[providerPrice] = price }
            }
        }
    }

    override fun findById(id: Int): Product? = withTransaction {
        table.selectAll().where { table.id.eq(id) }.firstOrNull()?.let {
            Product(
                name = it[name],
                sellingPrice = it[sellingPrice],
                providerPrice = it[providerPrice],
                inStockQuantity = it[inStockQuantity]
            )
        }
    }

    override fun deleteById(id: Int) {
        withTransaction {
            table.deleteWhere { table.id.eq(id) }
        }
    }

    override fun findALlIds(): List<Int> = withTransaction {
        table.selectAll()
    }.map { it[id] }
}