package io.drullar.inventar.shared

data class SaveOrderRequest(
    val productIdToQuantity: Map<Int, Int>
)

data class SaveProductRequest(
    val name: String,
    val inStockQuantity: Int = 0,
    val sellingPrice: Double? = null,
    val providerPrice: Double? = null,
    val categoryNames: Set<String>
)