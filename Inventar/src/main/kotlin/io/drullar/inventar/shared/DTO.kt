package io.drullar.inventar.shared

data class ProductDTO(
    val id: Int,
    val name: String,
    val inStockQuantity: Int,
    val sellingPrice: Double,
    val providerPrice: Double
)