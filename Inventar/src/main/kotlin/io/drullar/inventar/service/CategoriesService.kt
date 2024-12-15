package io.drullar.inventar.service

import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.persistence.repositories.CategoriesRepository

class CategoriesService {
    private val categoriesRepository = CategoriesRepository

    fun save(categoryName: String) =
        categoriesRepository.save(Category(categoryName))

    fun categoryExists(categoryName: String) =
        (categoriesRepository.getById(categoryName) != null)
}