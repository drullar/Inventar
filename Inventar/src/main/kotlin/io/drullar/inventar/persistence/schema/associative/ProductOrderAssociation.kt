package io.drullar.inventar.persistence.schema.associative

import io.drullar.inventar.persistence.Relation
import io.drullar.inventar.persistence.schema.Orders
import io.drullar.inventar.persistence.schema.Products
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

@Relation
object ProductOrderAssociation : Table("product_order") {
    val productUid = reference(
        "product_id",
        Products.uid,
        onUpdate = ReferenceOption.NO_ACTION,
        onDelete = ReferenceOption.NO_ACTION
    )
    val orderUid = reference("order_id", Orders.id, onDelete = ReferenceOption.CASCADE)
    val sellingPrice = decimal("product_price", Int.MAX_VALUE, 2)
    val orderedAmount = integer("ordered_amount")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(orderUid, productUid)
}