package io.drullar.inventar.ui.data

import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO

interface WindowPayload<T> {
    fun getData(): T
}

class EmptyPayload : WindowPayload<Unit> {
    override fun getData() = Unit
}

data class OrderWindowPayload(private val order: OrderDTO) : WindowPayload<OrderDTO> {
    override fun getData(): OrderDTO = order
}

data class ProductPayload(private val product: ProductDTO) : WindowPayload<ProductDTO> {
    override fun getData(): ProductDTO = product
}