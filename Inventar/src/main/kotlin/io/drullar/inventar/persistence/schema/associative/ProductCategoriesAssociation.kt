package io.drullar.inventar.persistence.schema.associative

import io.drullar.inventar.persistence.Relation
import io.drullar.inventar.persistence.schema.Categories
import org.jetbrains.exposed.sql.Table

@Relation
internal object ProductCategoriesAssociation : Table("product_categories") {
    val categoryName = reference("category_name", Categories.name)
    val productId = integer("category_product_id")//

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(categoryName, productId)
}