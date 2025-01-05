package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.persistence.schema.Categories
import io.drullar.inventar.persistence.schema.Categories.name
import io.drullar.inventar.shared.RepositoryResponse
import io.drullar.inventar.shared.response
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

//object CategoriesRepository : TODO Fix and implement
//    AbstractRepository<Categories, Category, String>(Categories) {
//
//    override fun save(model: Category): RepositoryResponse<Category> = response {
//        withTransaction {
//            Categories.insert {
//                it[name] = model.name
//            }.resultedValues?.first()?.let { transformResultRowToModel(it) }
//                ?: throw DatabaseException.PersistenceException("Couldn't save category with name ${model.name}")
//        }
//    }
//
//    override fun update(id: String, model: Category): RepositoryResponse<Category> {
//        withTransaction {
//            table.update(where = { table.name.eq(id) }) {
//                it[name] = model.name
//            }
//        }
//        return getById(id)
//    }
//
//    override fun getById(id: String) = response {
//        withTransaction {
//            table.selectAll().where { table.name.eq(id) }.firstOrNull()?.let {
//                transformResultRowToModel(it)
//            }
//                ?: throw DatabaseException.NoSuchElementFoundException("Category with id $id was not found")
//        }
//    }
//
//    override fun deleteById(id: String) = response {
//        withTransaction {
//            table.deleteWhere { table.name.eq(id) }
//        }
//        return@response
//    }
//
//    override fun transformResultRowToModel(row: ResultRow): Category = Category(row[name])
//}