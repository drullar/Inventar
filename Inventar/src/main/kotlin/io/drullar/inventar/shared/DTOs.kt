package io.drullar.inventar.shared

import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.ui.utils.Icons

data class ProductDTO(
    val uid: Int? = null,
    var name: String,
    var sellingPrice: Double = 0.0,
    var availableQuantity: Int = 0,
    val iconPath: String = Icons.PRODUCTS, // TODO change default value after creating a default value image
    var providerPrice: Double?,
    var barcode: String?,
    var categories: Set<Category> = emptySet()
)

data class OrderDTO(
    val orderId: Int,
    val productNameToQuantity: Map<String, Int>
)