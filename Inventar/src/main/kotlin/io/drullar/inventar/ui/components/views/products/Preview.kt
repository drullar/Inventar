package io.drullar.inventar.ui.components.views.products

import io.drullar.inventar.shared.ProductDTO

interface Preview<T> {
    fun getPreviewData(): T
}

class DetailedProductPreview(private val selectedProductDTO: ProductDTO) : Preview<ProductDTO> {
    override fun getPreviewData(): ProductDTO {
        return selectedProductDTO;
    }
}

class OrderCreationPreview : Preview<Any> {
    override fun getPreviewData(): Any {
        TODO("Not yet implemented")
    }
}