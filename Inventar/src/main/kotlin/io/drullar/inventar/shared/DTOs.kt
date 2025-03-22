package io.drullar.inventar.shared

import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.provider.getText
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDTO(
    val uid: Int,
    var name: String,
    var sellingPrice: BigDecimal = BigDecimal.valueOf(0.0),
    var availableQuantity: Int = 0,
    val iconPath: String = Icons.PRODUCTS, // TODO change default value after creating a default value image
    var providerPrice: BigDecimal? = null,
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
        categories = categories,
    )
}

data class ProductCreationDTO(
    var name: String,
    var sellingPrice: BigDecimal = BigDecimal.valueOf(0.0),
    var availableQuantity: Int = 0,
    val iconPath: String = Icons.PRODUCTS, //  TODO change default value after creating a default value image
    var providerPrice: BigDecimal? = null,
    var barcode: String? = null,
    var categories: Set<Category> = emptySet(),
)

data class OrderDTO(
    val orderId: Int,
    val productToQuantity: Map<ProductDTO, Int>,
    val creationDate: LocalDateTime,
    val status: OrderStatus
) {
    fun toOrderCreationDTO(): OrderCreationDTO = OrderCreationDTO(
        productToQuantity = productToQuantity,
        status = status
    )

    fun getTotalPrice(): BigDecimal {
        var cost = BigDecimal.valueOf(0.0)
        productToQuantity.forEach { (product, quantity) ->
            cost += (product.sellingPrice.multiply(
                BigDecimal(quantity)
            ))
        }
        return cost
    }
}

data class OrderCreationDTO(
    val productToQuantity: Map<ProductDTO, Int>,
    val status: OrderStatus
)

enum class OrderStatus(val text: Lazy<String>) {
    COMPLETED(lazy { getText("label.completed") }),
    TERMINATED(lazy { getText("label.terminated") }),
    DRAFT(lazy { getText("label.draft") })
}
