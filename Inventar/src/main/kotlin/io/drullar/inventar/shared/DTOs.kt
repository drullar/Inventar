package io.drullar.inventar.shared

import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.ui.utils.Icons
import java.time.LocalDateTime

data class ProductDTO(
    val uid: Int,
    var name: String,
    var sellingPrice: Double = 0.0,
    var availableQuantity: Int = 0,
    val iconPath: String = Icons.PRODUCTS, // TODO change default value after creating a default value image
    var providerPrice: Double? = null,
    var barcode: String? = null,
    var categories: Set<Category> = emptySet()
) {
    fun toProductCreationDTO() = ProductCreationDTO(
        name = name,
        sellingPrice = sellingPrice,
        availableQuantity = availableQuantity,
        iconPath = iconPath,
        providerPrice = providerPrice,
        barcode = barcode,
        categories = categories
    )
}

data class ProductCreationDTO(
    var name: String,
    var sellingPrice: Double = 0.0,
    var availableQuantity: Int = 0,
    val iconPath: String = Icons.PRODUCTS, //  TODO change default value after creating a default value image
    var providerPrice: Double? = null,
    var barcode: String? = null,
    var categories: Set<Category> = emptySet()
)

data class OrderDTO(
    val orderId: Int,
    val productToQuantity: MutableMap<ProductDTO, Int>,
    val creationDate: LocalDateTime,
    val status: OrderStatus
) {
    fun toOrderCreationDTO(): OrderCreationDTO = OrderCreationDTO(
        productToQuantity = productToQuantity,
        status = status
    )

    fun getTotalPrice(): Double {
        var cost: Double = 0.0
        productToQuantity.forEach { (product, quantity) ->
            cost += (product.sellingPrice * quantity)
        }
        return cost
    }
}

data class OrderCreationDTO(
    val productToQuantity: Map<ProductDTO, Int>,
    val status: OrderStatus
)

enum class OrderStatus {
    COMPLETED,
    CANCELED,
    DRAFT
}
