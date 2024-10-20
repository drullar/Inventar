package io.drullar.inventar.persistence.model

import io.drullar.inventar.persistence.model.id.ProductOrderKey

data class ProductOrderAssociationModel(
    val productOrderKey: ProductOrderKey,
    val productName: String?,
    val productSellingPrice: Double,
    val orderedAmount: Int
)