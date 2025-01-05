package io.drullar.inventar.persistence.schema

import io.drullar.inventar.persistence.Relation
import org.jetbrains.exposed.sql.Table

@Relation
object Products : Table("products") {
    val uid = integer("id").autoIncrement().uniqueIndex()
    val name = varchar("name", NAME_MAX_LENGTH)
    val availableQuantity = integer("quantity")
    val sellingPrice = double("selling_price")
    val providerPrice = double("provider_price").nullable()
    val barcode = varchar("barcode", BARCODE_LENGTH).nullable()

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(uid)
}