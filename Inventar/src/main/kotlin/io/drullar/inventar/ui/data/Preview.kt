package io.drullar.inventar.ui.data

import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO

abstract class Preview<T> {
    abstract fun getPreviewData(): T
    abstract fun updatePreviewData(data: T)
}

class DetailedProductPreview(private var selectedProductDTO: ProductDTO) :
    Preview<ProductDTO>() {
    override fun getPreviewData(): ProductDTO = selectedProductDTO

    override fun updatePreviewData(data: ProductDTO) {
        selectedProductDTO = data
    }
}

class OrderDetailsPreview(private val order: OrderDTO) : Preview<OrderDTO>() {
    override fun getPreviewData(): OrderDTO = order

    override fun updatePreviewData(data: OrderDTO) {
        throw NotImplementedError()
    }
}

class OrdersListPreview(private var orders: List<OrderDTO>) : Preview<List<OrderDTO>>() {
    override fun getPreviewData(): List<OrderDTO> = orders

    override fun updatePreviewData(data: List<OrderDTO>) {
        TODO("Not yet implemented")
    }

}