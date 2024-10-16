package io.drullar.inventar.persistence.service

import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.persistence.model.Product
import io.drullar.inventar.persistence.repositories.CategoryPersistenceRepository
import io.drullar.inventar.persistence.repositories.ProductPersistenceRepository

object ProductPersistenceService {
    private val productRepository = ProductPersistenceRepository
    private val categoryRepository = CategoryPersistenceRepository

    fun save(request: SaveProductRequest) {
        productRepository.save(request.product)
        request.categories.map {
            val categoryName = it.name
            categoryRepository.findById(categoryName).let { category ->
                if (category == null)
                    categoryRepository.save(Category(name = categoryName))
            }
        }
    }
}

data class SaveProductRequest(
    val product: Product,
    val categories: Set<Category>
)