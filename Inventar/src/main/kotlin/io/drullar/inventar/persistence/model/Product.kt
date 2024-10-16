package io.drullar.inventar.persistence.model

class Product(
    val name: String,
    val inStockQuantity: Int = 0,
    val sellingPrice: Double? = null,
    val providerPrice: Double? = null
)