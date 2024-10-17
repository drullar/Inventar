package io.drullar.inventar.persistence.schema.mapping

import io.drullar.inventar.persistence.Relation
import io.drullar.inventar.persistence.schema.Categories
import io.drullar.inventar.persistence.schema.Products
import org.jetbrains.exposed.sql.Table

@Relation
internal object ProductCategoriesMapping : Table("product_categories") {
    val categoryName = reference("category_name", Categories.name)
    val productId = reference("product_id", Products.id)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(categoryName, productId)
}