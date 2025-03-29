package io.drullar.inventar.unit.utils

import io.drullar.inventar.shared.ProductDTO
import java.math.BigDecimal
import kotlin.random.Random

object DTOFactory {
    fun product(
        uid: Int = Random.nextInt(),
        name: String = "Product1",
        availableQuantity: Int = 1,
        sellingPrice: BigDecimal = 0.0.toBigDecimal()
    ) = ProductDTO(
        uid = uid,
        name = name,
        availableQuantity = availableQuantity,
        sellingPrice = sellingPrice
    )
}