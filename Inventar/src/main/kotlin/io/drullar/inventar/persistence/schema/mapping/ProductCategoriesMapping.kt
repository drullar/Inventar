package io.drullar.inventar.persistence.schema.mapping

import io.drullar.inventar.persistence.schema.Categories
import io.drullar.inventar.persistence.schema.Products
import org.jetbrains.exposed.sql.Table

object ProductCategoriesMapping : Table("product_categories") {
    private val category = reference("category_name", Categories.name)
    private val product = reference("product_id", Products.id)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(category, product)
}