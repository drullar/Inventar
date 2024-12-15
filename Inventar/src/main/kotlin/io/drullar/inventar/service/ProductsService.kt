package io.drullar.inventar.service

import io.drullar.inventar.persistence.repositories.ProductCategoryMappingAssociation
import io.drullar.inventar.persistence.repositories.ProductsService
import io.drullar.inventar.shared.ProductDTO

class ProductsService {
    private val productRepository = ProductsService
    private val productCategoryMappingAssociation = ProductCategoryMappingAssociation
    private val categoriesService by lazy { CategoriesService() }

    fun save(product: ProductDTO) = productRepository.save(product)

    fun getProductById(id: Int): ProductDTO? = productRepository.getById(id)

    fun getAll(): List<ProductDTO> = productRepository.getAll()

    fun delete(id: Int) = productRepository.deleteById(id)

    fun update(id: Int, product: ProductDTO) = NotImplementedError() //TODO
}


