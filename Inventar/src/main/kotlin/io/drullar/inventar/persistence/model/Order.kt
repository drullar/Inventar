package io.drullar.inventar.persistence.model

import java.time.LocalDateTime
import java.util.UUID

data class Order(
    val id: UUID,
    val products: List<Product>,
    val date: LocalDateTime,
    val status: OrderStatus
)

enum class OrderStatus {
    COMPLETED,
    CANCELED
}
