package io.drullar.inventar.persistence.model

import java.time.LocalDateTime

data class Order(
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val status: OrderStatus,
    val totalPrice: Double
)

enum class OrderStatus {
    COMPLETED,
    CANCELED,
    DRAFT
}
