package io.drullar.inventar.persistence

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.drullar.inventar.persistence.repositories.ProductsService
import io.drullar.inventar.persistence.utils.DTOFactory
import io.drullar.inventar.shared.ProductDTO
import org.junit.jupiter.api.AfterEach
import kotlin.test.Test

class TestProductsService : AbstractPersistenceTest() {

    private val productsService = ProductsService

    @AfterEach
    override fun cleanUp() {
        productsService.deleteAll()
    }

    @Test
    fun saveAndFindById() {

        val barcode = "023120322134"
        val id = productsService.save(
            ProductDTO(
                name = "New Product",
                sellingPrice = 10.0,
                providerPrice = 8.0,
                availableQuantity = 20,
                barcode = barcode
            )
        )

        val savedEntity = productsService.getById(id)
        assertThat(savedEntity).isNotNull()
        assertThat(savedEntity!!.name).isEqualTo("New Product")
        assertThat(savedEntity.sellingPrice).isEqualTo(10.0)
        assertThat(savedEntity.providerPrice).isEqualTo(8.0)
        assertThat(savedEntity.availableQuantity).isEqualTo(20)
        assertThat(savedEntity.barcode).isEqualTo(barcode)

    }

    @Test
    fun autoIncrementedId() {
        val firstId = productsService.save(
            DTOFactory.createProductDTO()
        )
        val secondId = productsService.save(
            DTOFactory.createProductDTO()
        )

        assertThat(firstId).isNotNull()
        assertThat(secondId).isNotNull()
        assertThat(secondId).isEqualTo(firstId + 1)
    }

    @Test
    fun delete() {
        for (i in 1..5) {
            productsService.save(
                DTOFactory.createProductDTO()
            )
        }

        for (i in 1..5) {
            productsService.deleteById(i)
            assertThat(productsService.getById(i)).isNull()
        }
    }

    @Test
    fun deleteAll() {
        for (i in 1..10) {
            productsService.save(DTOFactory.createProductDTO())
        }
        assertThat(productsService.getAll().count()).isEqualTo(10)
        productsService.deleteAll()
        assertThat(productsService.getAll().count()).isEqualTo(0)
    }

    @Test
    fun getAll() {
        for (i in 1..10) {
            productsService.save(DTOFactory.createProductDTO())
        }
        assertThat(productsService.getAll().count()).isEqualTo(10)
    }

    @Test
    fun update() {
        val id = productsService.save(
            DTOFactory.createProductDTO(
                name = "Some name",
                sellingPrice = 10.0
            )
        )

        productsService.update(
            id,
            DTOFactory.createProductDTO("New name", sellingPrice = 10.0)
        )

        val updatedProduct = productsService.getById(id)
        assertThat(updatedProduct!!).isNotNull()
        assertThat(updatedProduct.name).isEqualTo("New name")
        assertThat(updatedProduct.sellingPrice).isEqualTo(10.0)
    }
}