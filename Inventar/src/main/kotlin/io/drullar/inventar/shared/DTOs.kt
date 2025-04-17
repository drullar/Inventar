package io.drullar.inventar.shared

import androidx.compose.ui.graphics.Color
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.Colors
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class ProductDTO(
    val uid: Int,
    var name: String,
    var sellingPrice: BigDecimal = BigDecimal.valueOf(0.0),
    var availableQuantity: Int = 0,
    val iconPath: String = Icons.PRODUCTS, // TODO change default value after creating a default value image
    var providerPrice: BigDecimal? = null,
    var barcode: String? = null,
) {
    fun toProductCreationDTO() = ProductCreationDTO(
        name = name,
        sellingPrice = sellingPrice,
        availableQuantity = availableQuantity,
        iconPath = iconPath,
        providerPrice = providerPrice,
        barcode = barcode,
    )
}

data class ProductCreationDTO(
    var name: String,
    var sellingPrice: BigDecimal = BigDecimal.valueOf(0.0),
    var availableQuantity: Int = 0,
    val iconPath: String = Icons.PRODUCTS, //  TODO change default value after creating a default value image
    var providerPrice: BigDecimal? = null,
    var barcode: String? = null,
    val isMarkedForDeletion: Boolean = false
)

data class OrderDTO(
    val orderId: Int,

    val productToQuantity: Map<ProductDTO, Int>,
    val creationDate: LocalDateTime,
    val status: OrderStatus
) {
    fun toOrderCreationDTO(): OrderCreationDTO = OrderCreationDTO(
        productToQuantity = productToQuantity,
        status = status,
        creationDate = creationDate
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
    val status: OrderStatus,
    val creationDate: LocalDateTime = LocalDateTime.now() // required for testing purposes, otherwise default in the model would have sufficed
)

data class ProductSoldAmountDTO(
    val productId: Int,
    val soldQuantity: Int,
    val fromDate: LocalDate,
    val untilDate: LocalDate
)

enum class OrderStatus(val text: Lazy<String>, val associatedColor: Lazy<Color>) {
    COMPLETED(lazy { getText("label.completed") }, lazy { Colors.DarkGreen }),
    TERMINATED(lazy { getText("label.terminated") }, lazy { Color.Red }),
    DRAFT(lazy { getText("label.draft") }, lazy { Color.Gray })
}