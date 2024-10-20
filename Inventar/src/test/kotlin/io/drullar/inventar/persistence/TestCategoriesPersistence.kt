package io.drullar.inventar.persistence

import assertk.assertThat
import assertk.assertions.*
import io.drullar.inventar.persistence.model.Category
import io.drullar.inventar.persistence.repositories.CategoryPersistenceRepository
import io.drullar.inventar.persistence.utils.TestPersistenceConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test


class TestCategoriesPersistence : AbstractPersistenceTest() {

    private val repository = CategoryPersistenceRepository

    @AfterEach
    override fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun create() {
        val id = repository.save(Category("Category"))
        assertThat(id).isNotNull()
        assertThat(repository.findById(id)).isNotNull()
        assertThrows<Exception> {
            repository.save(Category("Category"))
        }
    }

    @Test
    fun update() {
        val id = repository.save(Category("Category"))
        assertThat(repository.findById(id)).isNotNull()

        repository.update(id, Category("NewCategory"))
        assertThat(repository.findById(id)).isNull()
        assertThat(repository.findById("NewCategory")).isNotNull()
    }

    @Test
    fun findById() {
        val id = repository.save(Category("Category"))
        assertThat(repository.findById(id)).isNotNull()
    }

    @Test
    fun findAll() {
        val categoriesList = mutableListOf<Category>()
        for (i in 1..10) {
            val category = Category("Category${i}")
            categoriesList.add(category)
            repository.save(category)
        }

        val all = repository.findAll()
        assertThat(all.count()).isEqualTo(10)
        assertThat(all.containsAll(categoriesList)).isTrue()
    }

    @Test
    fun delete() {
        val id = repository.save(Category("Category"))
        assertThat(repository.findById(id)).isNotNull()
        repository.deleteById(id)
        assertThat(repository.findById(id)).isNull()
    }

    @Test
    fun deleteAll() {
        for (i in 1..10)
            repository.save(Category("$i"))
        val all = repository.findAll()
        assertThat(all.count()).isEqualTo(10)
        repository.deleteAll()
        assertThat(repository.findAll().count()).isEqualTo(0)
    }
}