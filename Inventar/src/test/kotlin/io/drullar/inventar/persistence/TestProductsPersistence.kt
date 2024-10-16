package io.drullar.inventar.persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.drullar.inventar.persistence.model.Product
import org.junit.jupiter.api.AfterEach
import kotlin.test.Test

class TestProductsPersistence : AbstractPersistenceTest() {

    @AfterEach
    override fun cleanUp() {
        productsRepository.deleteAll()
    }

    @Test
    fun saveAndFindById() {
        val id = productsRepository.save(
            Product(
                name = "New Product",
                sellingPrice = 10.0,
                providerPrice = 8.0,
                inStockQuantity = 20
            )
        )

        val savedEntity = productsRepository.findById(id)
        assertThat(savedEntity).isNotNull()
        assertThat(savedEntity!!.name).isEqualTo("New Product")
        assertThat(savedEntity!!.sellingPrice).isEqualTo(10.0)
        assertThat(savedEntity!!.providerPrice).isEqualTo(8.0)
        assertThat(savedEntity!!.inStockQuantity).isEqualTo(20)

    }

    @Test
    fun autoIncrementedId() {
        val firstId = productsRepository.save(
            Product(
                name = "New Product",
                sellingPrice = 10.0,
                providerPrice = 8.0,
                inStockQuantity = 20
            )
        )
        val secondId = productsRepository.save(
            Product(
                name = "New Product",
                sellingPrice = 10.0,
                providerPrice = 8.0,
                inStockQuantity = 20
            )
        )

        assertThat(firstId).isNotNull()
        assertThat(secondId).isNotNull()
        assertThat(secondId).isEqualTo(firstId + 1)
    }

    @Test
    fun delete() {
        for (i in 1..5)
            productsRepository.save(Product("asdfs", 1, 1.0, 2.0))
        productsRepository.deleteById(1)
        assertThat(productsRepository.findById(1)).isNull()
        productsRepository.deleteAll()
        for (i in 2..5)
            assertThat(productsRepository.findById(i)).isNull()
    }
}