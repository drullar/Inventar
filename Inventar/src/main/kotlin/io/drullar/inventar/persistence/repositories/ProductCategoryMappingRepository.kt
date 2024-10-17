package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.model.mapping.ProductCategoryPair
import io.drullar.inventar.persistence.schema.mapping.ProductCategoriesMapping
import io.drullar.inventar.persistence.schema.mapping.ProductCategoriesMapping.categoryName
import io.drullar.inventar.persistence.schema.mapping.ProductCategoriesMapping.productId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

internal object ProductCategoryMappingRepository :
    AbstractPersistenceRepository<ProductCategoriesMapping, ProductCategoryPair, ProductCategoryPair>(
        ProductCategoriesMapping
    ) {
    private val mappingTable = ProductCategoriesMapping

    override fun save(payload: ProductCategoryPair): ProductCategoryPair = withTransaction {
        mappingTable.insert {
            it[categoryName] = payload.categoryName
            it[productId] = payload.productId
        }
        payload
    }

    override fun update(id: ProductCategoryPair, payload: ProductCategoryPair) {
        withTransaction {
            table.update(where = {
                table.categoryName.eq(id.categoryName)
                    .and(table.productId.eq(id.productId))
            }) {
                it[categoryName] = payload.categoryName
                it[productId] = payload.productId
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