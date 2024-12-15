package io.drullar.inventar.persistence.utils

import io.drullar.inventar.shared.ProductDTO

object DTOFactory {
    fun createProductDTO(
        name: String = "asdfs",
        sellingPrice: Double = 0.0,
        providerPrice: Double? = 0.0,
        barcode: String? = null,
        availableQuantity: Int = 0
    ) = ProductDTO(
        name = name,
        sellingPrice = sellingPrice,
        availableQuantity = availableQuantity,
        providerPrice = providerPrice,
        barcode = barcode
    )
}