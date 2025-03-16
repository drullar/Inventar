package io.drullar.inventar.persistence.schema

import io.drullar.inventar.persistence.Relation
import org.jetbrains.exposed.sql.Table

@Relation
object Products : Table("products") {
    val uid = integer("id").autoIncrement().uniqueIndex()
    val name = varchar("name", NAME_MAX_LENGTH)
    val availableQuantity = integer("quantity")
    val providerPrice = decimal("provider_price", Int.MAX_VALUE, 2).nullable()
    val sellingPrice = decimal("selling_price", Int.MAX_VALUE, 2)
    val barcode = varchar("barcode", BARCODE_LENGTH).nullable()
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(uid)
}