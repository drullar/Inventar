package io.drullar.inventar.persistence

import assertk.assertThat
import assertk.assertions.*
import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.persistence.repositories.CategoriesRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test


class TestCategoriesPersistence : AbstractPersistenceTest() {

    private val repository = CategoriesRepository

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun create() {
        val id = repository.save(Category("Category"))
        assertThat(id).isNotNull()
        assertThat(repository.getById(id)).isNotNull()
        assertThrows<Exception> {
            repository.save(Category("Category"))
        }
    }

    @Test
    fun update() {
        val id = repository.save(Category("Category"))
        assertThat(repository.getById(id)).isNotNull()

        repository.update(id, Category("NewCategory"))
        assertThat(repository.getById(id)).isNull()
        assertThat(repository.getById("NewCategory")).isNotNull()
    }

    @Test
    fun findById() {
        val id = repository.save(Category("Category"))
        assertThat(repository.getById(id)).isNotNull()
    }

    @Test
    fun findAll() {
        val categoriesList = mutableListOf<Category>()
        for (i in 1..10) {
            val category = Category("Category${i}")
            categoriesList.add(category)
            repository.save(category)
        }

        val all = repository.getAll()
        assertThat(all.count()).isEqualTo(10)
        assertThat(all.containsAll(categoriesList)).isTrue()
    }

    @Test
    fun delete() {
        val id = repository.save(Category("Category"))
        assertThat(repository.getById(id)).isNotNull()
        repository.deleteById(id)
        assertThat(repository.getById(id)).isNull()
    }

    @Test
    fun deleteAll() {
        for (i in 1..10)
            repository.save(Category("$i"))
        val all = repository.getAll()
        assertThat(all.count()).isEqualTo(10)
        repository.deleteAll()
        assertThat(repository.getAll().count()).isEqualTo(0)
    }
}