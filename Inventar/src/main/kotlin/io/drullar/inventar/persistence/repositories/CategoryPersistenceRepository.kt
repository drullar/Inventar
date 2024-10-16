package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.PersistenceException
import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.persistence.schema.Categories
import io.drullar.inventar.persistence.schema.Categories.name
import io.drullar.inventar.persistence.schema.Products
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

internal object CategoryPersistenceRepository :
    AbstractPersistenceRepository<Categories, Category, String>(Categories) {

    override fun save(payload: Category): String = withTransaction {
        Categories.insert {
            it[name] = name
        }.resultedValues?.first()?.let { it[name] } ?: throw PersistenceException()
    }

    override fun update(id: String, payload: Category) {
        withTransaction {
            table.update(where = { table.name.eq(id) }) {
                it[name] = payload.name
            }
        }
    }

    override fun findById(id: String): Category? = withTransaction {
        table.selectAll().where { table.name.eq(id) }.firstOrNull()?.let {
            Category(
                name = it[Products.name]
            )
        }
    }

    override fun deleteById(id: String) {
        withTransaction {
            table.deleteWhere { table.name.eq(id) }
        }
    }

    override fun findALlIds(): List<String> = withTransaction {
        table.selectAll()
    }.map { it[name] }
}