package io.drullar.inventar.persistence.schema

import io.drullar.inventar.persistence.Relation
import io.drullar.inventar.persistence.model.OrderStatus
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Columns: [creationDate], [totalPrice], [orderStatus]
 */
@Relation
internal object Orders : Table(name = "orders") {
    val id = uuid(name = "id").autoGenerate()
    val creationDate = datetime("creation_date").default(LocalDateTime.now())
    val totalPrice = double("total_price")
    val orderStatus = enumeration<OrderStatus>("order_status")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}