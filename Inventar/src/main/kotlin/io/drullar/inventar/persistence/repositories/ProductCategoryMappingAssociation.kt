package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.model.id.ProductCategoryPair
import io.drullar.inventar.persistence.schema.associative.ProductCategoriesAssociation
import io.drullar.inventar.persistence.schema.associative.ProductCategoriesAssociation.categoryName
import io.drullar.inventar.persistence.schema.associative.ProductCategoriesAssociation.productId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal object ProductCategoryMappingAssociation :
    AbstractPersistenceRepository<ProductCategoriesAssociation, ProductCategoryPair, ProductCategoryPair>(
        ProductCategoriesAssociation
    ) {
    private val mappingTable = ProductCategoriesAssociation

    override fun save(model: ProductCategoryPair): ProductCategoryPair = withTransaction {
        mappingTable.insert {
            it[categoryName] = model.categoryName
            it[productId] = model.productId
        }
        model
    }

    override fun update(id: ProductCategoryPair, model: ProductCategoryPair) {
        withTransaction {
            table.update(where = {
                table.categoryName.eq(id.categoryName)
                    .and(table.productId.eq(id.productId))
            }) {
                it[categoryName] = model.categoryName
                it[productId] = model.productId
            }
        }
    }

    override fun findById(id: ProductCategoryPair): ProductCategoryPair? = withTransaction {
        table.selectAll().where {
            table.categoryName.eq(id.categoryName)
                .and(table.productId.eq(id.productId))
        }.firstOrNull().let {
            if (it != null) id
            else null
        }
    }

    override fun deleteById(id: ProductCategoryPair) {
        withTransaction {
            table.deleteWhere {
                table.categoryName.eq(id.categoryName).and(table.productId.eq(id.productId))
            }
        }
    }

    override fun transformResultRowToModel(row: ResultRow): ProductCategoryPair = ProductCategoryPair(
        productId = row[productId],
        categoryName = row[categoryName]
    )
}