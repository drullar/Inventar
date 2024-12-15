package io.drullar.inventar.persistence.repositories

import io.drullar.inventar.persistence.model.Order
import io.drullar.inventar.persistence.schema.Orders
import io.drullar.inventar.persistence.schema.Orders.creationDate
import io.drullar.inventar.persistence.schema.Orders.id
import io.drullar.inventar.persistence.schema.Orders.orderStatus
import io.drullar.inventar.persistence.schema.Orders.totalPrice
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

internal object OrderRepository : AbstractPersistenceRepository<Orders, Order, UUID>(Orders) {
    override fun save(model: Order): UUID = withTransaction {
        table.insert {
            it[orderStatus] = model.status
            it[totalPrice] = model.totalPrice
        }.resultedValues!!.first().let { it[id] }
    }

    override fun transformResultRowToModel(row: ResultRow): Order = Order(
        creationDate = row[creationDate],
        status = row[orderStatus],
        totalPrice = row[totalPrice]
    )

    override fun deleteById(id: UUID) {
        withTransaction {
            table.deleteWhere { table.id.eq(id) }
        }
    }

    override fun getById(id: UUID): Order? = withTransaction {
        table.selectAll().where { table.id.eq(id) }.first().let {
            transformResultRowToModel(it)
        }
    }

    override fun update(id: UUID, model: Order) {
        withTransaction {
            table.update(where = { table.id.eq(id) }) {
                it[orderStatus] = model.status
                it[totalPrice] = model.totalPrice
                // Creation date should not be updated
            }
        }
    }
}