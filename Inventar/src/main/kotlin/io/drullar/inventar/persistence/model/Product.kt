package io.drullar.inventar.persistence.model

class Product(
    val id: Int? = null,
    val name: String,
    val inStockQuantity: Int = 0,
    val sellingPrice: Double,
    val providerPrice: Double
)