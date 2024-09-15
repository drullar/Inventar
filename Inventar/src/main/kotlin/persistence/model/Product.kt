package org.example.persistence.model

import org.example.persistence.NAME_MAX_LENGTH
import org.jetbrains.exposed.sql.Table

object Product : Table("product") {
    val id = uuid("id").databaseGenerated()
    val name = varchar("name", NAME_MAX_LENGTH)
    val inStockQuantity = integer("inStock").default(0)
    val sellingPrice = double("sellingPrice").default(0.0)
    val providerPrice = double("providerPrice").default(0.0)

    override val primaryKey = PrimaryKey(id)
}