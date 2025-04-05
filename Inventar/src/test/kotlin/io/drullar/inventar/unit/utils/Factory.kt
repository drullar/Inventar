package io.drullar.inventar.unit.utils

import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductDTO
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

object Factory {

    fun createProduct(
        uid: Int = Random.nextInt(),
        name: String = "Product1",
        availableQuantity: Int = 1,
        sellingPrice: BigDecimal = 0.0.toBigDecimal(),
        barcode: String? = null
    ) = ProductDTO(
        uid = uid,
        name = name,
        availableQuantity = availableQuantity,
        sellingPrice = sellingPrice,
        barcode = barcode
    )

    fun createOrder(
        orderId: Int = Random.nextInt(),
        productToQuantity: Map<ProductDTO, Int> = mapOf(createProduct() to 1),
        creationDate: LocalDateTime = LocalDateTime.now(),
        status: OrderStatus = OrderStatus.DRAFT
    ) = OrderDTO(
        orderId = orderId,
        productToQuantity = productToQuantity,
        creationDate = creationDate,
        status = status
    )
}