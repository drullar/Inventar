package io.drullar.inventar.persistence.schema

import io.drullar.inventar.persistence.Relation
import io.drullar.inventar.shared.OrderStatus
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Columns: [creationDate], [orderStatus]
 */
@Relation
object Orders : Table(name = "orders") {
    val id = integer(name = "id").autoIncrement().uniqueIndex()
    val creationDate = datetime("creation_date")
    val orderStatus = enumeration<OrderStatus>("order_status")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}