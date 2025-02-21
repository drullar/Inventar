package io.drullar.inventar.ui.components.views.default

import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO

abstract class Preview<T> {
    abstract fun getPreviewData(): T
    abstract fun updatePreviewData(data: T)
}

data class DetailedProductPreview(private var selectedProductDTO: ProductDTO) :
    Preview<ProductDTO>() {
    override fun getPreviewData(): ProductDTO = selectedProductDTO

    override fun updatePreviewData(data: ProductDTO) {
        selectedProductDTO = data
    }
}

data class OrderCreationPreview(private val order: OrderDTO) : Preview<OrderDTO>() {
    override fun getPreviewData(): OrderDTO = order

    override fun updatePreviewData(data: OrderDTO) {
        throw NotImplementedError()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is OrderCreationPreview) return false
        if (other.order.productToQuantity.equals(this.order.productToQuantity)) return true
        return false
    }
}

data class OrdersListPreview(private var orders: List<OrderDTO>) : Preview<List<OrderDTO>>() {
    override fun getPreviewData(): List<OrderDTO> = orders

    override fun updatePreviewData(data: List<OrderDTO>) {
        TODO("Not yet implemented")
    }

}