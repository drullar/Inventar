package org.example.persistence.model

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object Products : UUIDTable("product") {
    val name = varchar("name", NAME_MAX_LENGTH)
    val inStockQuantity = integer("inStock").default(0)
    val sellingPrice = double("sellingPrice").default(0.0)
    val providerPrice = double("providerPrice").default(0.0)
}

class Product(id: EntityID<UUID>) : UUIDEntity(id) {
    var name by Products.name
    var inStockQuantity by Products.inStockQuantity
    var sellingPrice by Products.sellingPrice
    var providerPrice by Products.providerPrice

    companion object : EntityClass<UUID, Product>(Products)
}