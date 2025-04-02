package io.drullar.inventar.ui.data

import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO

interface Preview<T> {
    fun getData(): PreviewData<T>
}

abstract class AbstractPreview<T>(data: T) : Preview<T> {
    private val previewData = PreviewData<T>(data)
    override fun getData(): PreviewData<T> = previewData

    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return previewData.hashCode()
    }
}

data class DetailedProductPreview(private val productDTO: ProductDTO) :
    AbstractPreview<ProductDTO>(productDTO)

data class OrderDetailsPreview(private val order: OrderDTO) :
    AbstractPreview<OrderDTO>(order)

data class OrdersListPreview(private val orders: List<OrderDTO>) :
    AbstractPreview<List<OrderDTO>>(orders)

data class PreviewData<T>(val data: T)