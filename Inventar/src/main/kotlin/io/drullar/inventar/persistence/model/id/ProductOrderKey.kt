package io.drullar.inventar.persistence.model.id

import java.util.*

data class ProductOrderKey(
    val productId: Int,
    val orderId: UUID
)
