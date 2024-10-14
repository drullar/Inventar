package io.drullar.inventar.persistence.model

import org.jetbrains.exposed.sql.Table

object Products : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val name = varchar("name", NAME_MAX_LENGTH)
    val inStockQuantity = integer("quantity").default(0)
    val sellingPrice = double("selling_price")
    val providerPrice = double("provider_price")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}