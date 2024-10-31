package io.drullar.inventar.service

import io.drullar.inventar.persistence.model.Product
import io.drullar.inventar.persistence.model.id.ProductCategoryPair
import io.drullar.inventar.persistence.repositories.ProductCategoryMappingAssociation
import io.drullar.inventar.persistence.repositories.ProductsRepository
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.SaveProductRequest

class ProductsService {
    private val productRepository = ProductsRepository
    private val productCategoryMappingAssociation = ProductCategoryMappingAssociation
    private val categoriesService by lazy { CategoriesService() }

    fun save(request: SaveProductRequest) {
        val productId = productRepository.save(
            Product(
                name = request.name,
                inStockQuantity = request.inStockQuantity,
                sellingPrice = request.sellingPrice ?: 0.0,
                providerPrice = request.providerPrice ?: 0.0
            )
        )

        request.categoryNames.forEach { categoryName ->
            if (!categoriesService.categoryExists(categoryName))
                categoriesService.save(categoryName)
            productCategoryMappingAssociation.save(ProductCategoryPair(productId, categoryName))
        }
    }

    fun getProduct(id: Int): ProductDTO? = productRepository.findById(id)?.let {
        ProductDTO(
            id = id,
            name = it.name,
            providerPrice = it.providerPrice,
            sellingPrice = it.sellingPrice,
            inStockQuantity = it.inStockQuantity
        )
    }
}

