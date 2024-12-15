package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.PersistenceException
import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.persistence.schema.Categories
import io.drullar.inventar.persistence.schema.Categories.name
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal object CategoriesRepository :
    AbstractPersistenceRepository<Categories, Category, String>(Categories) {

    override fun save(model: Category): String = withTransaction {
        Categories.insert {
            it[name] = model.name
        }.resultedValues?.first()?.let { it[name] } ?: throw PersistenceException()
    }

    override fun update(id: String, model: Category) {
        withTransaction {
            table.update(where = { table.name.eq(id) }) {
                it[name] = model.name
            }
        }
    }

    override fun getById(id: String): Category? = withTransaction {
        table.selectAll().where { table.name.eq(id) }.firstOrNull()?.let {
            Category(
                name = it[name]
            )
        }
    }

    override fun deleteById(id: String) {
        withTransaction {
            table.deleteWhere { table.name.eq(id) }
        }
    }

    override fun transformResultRowToModel(row: ResultRow): Category = Category(row[name])
}