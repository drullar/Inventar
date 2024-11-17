package io.drullar.inventar.payload

import io.drullar.inventar.ui.utils.Icons

/**
 * Payload used for persistence and detailed display purposes
 */
data class ProductDetailedPayload(
    var name: String,
    var sellingPrice: Double = 0.0,
    var availableQuantity: Int = 0,
    var iconPath: String = Icons.PRODUCTS_ICON, // TODO change default value after creating a default value image
    var providerPrice: Double? = null,
    var barcode: String? = null
)