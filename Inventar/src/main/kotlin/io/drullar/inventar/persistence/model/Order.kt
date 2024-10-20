package io.drullar.inventar.persistence.model

import java.time.LocalDateTime

data class Order(
    val date: LocalDateTime,
    val status: OrderStatus,
    val totalPrice: Double
)

enum class OrderStatus {
    COMPLETED,
    CANCELED,
    DRAFT
}
