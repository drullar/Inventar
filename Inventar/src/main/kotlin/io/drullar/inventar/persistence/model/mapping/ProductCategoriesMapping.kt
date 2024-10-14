package io.drullar.inventar.persistence.model.mapping

import io.drullar.inventar.persistence.model.Categories
import io.drullar.inventar.persistence.model.Products
import org.jetbrains.exposed.sql.Table

object ProductCategoriesMapping : Table("product_categories") {
    val category = reference("category", Categories.name)
    val product = reference("product", Products.id)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(category, product)
}