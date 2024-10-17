package io.drullar.inventar.persistence.schema

import io.drullar.inventar.persistence.Relation
import org.jetbrains.exposed.sql.Table

@Relation
object Products : Table("products") {
    val id = integer("id").autoIncrement().uniqueIndex()
    val name = varchar("name", NAME_MAX_LENGTH)
    val inStockQuantity = integer("quantity").default(0)
    val sellingPrice = double("selling_price").default(0.0)
    val providerPrice = double("provider_price").default(0.0)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}