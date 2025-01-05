package io.drullar.inventar.persistence

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import io.drullar.inventar.persistence.repositories.ProductsRepository
import io.drullar.inventar.persistence.schema.Products
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.RepositoryResponse
import io.drullar.inventar.shared.getDataOnSuccessOrNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestProductRepository : AbstractPersistenceTest() {

    private val productRepository = ProductsRepository

    @AfterEach
    fun cleanup() {
        productRepository.deleteAll()
        val table = Products.autoIncColumn!!.table
        println()
    }

    @Test
    fun save() {
        val result = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        )
        assertThat(result).isInstanceOf(RepositoryResponse.Success::class)

        val data = result.getDataOnSuccessOrNull()
        assertThat(data).isNotNull()
        assertThat(data!!.name).isEqualTo("asdfs")
        assertThat(data.availableQuantity).isEqualTo(10)
        assertThat(data.barcode).isEqualTo("021321321")
        assertThat(data.sellingPrice).isEqualTo(0.0)
        assertThat(data.providerPrice).isNull()
        assertThat(data.uid).isNotNull()
    }

    @Test
    fun update() {
        val createResult = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        )
        assertThat(createResult).isInstanceOf(RepositoryResponse.Success::class)

        val data = createResult.getDataOnSuccessOrNull()
        assertThat(data).isNotNull()
        val productId = data!!.uid
        assertThat(productId).isNotNull()

        val updateResult =
            productRepository.update(
                productId!!,
                data.toProductCreationDTO().copy(
                    name = "new name",
                    sellingPrice = 2.0,
                    providerPrice = 0.0,
                    barcode = "3213121"
                )
            )

        val updatedProduct = updateResult.getDataOnSuccessOrNull()
        assertThat(updatedProduct).isNotNull()
        assertThat(updatedProduct!!.name).isEqualTo("new name")
        assertThat(updatedProduct.sellingPrice).isEqualTo(2.0)
        assertThat(updatedProduct.providerPrice).isEqualTo(0.0)
        assertThat(updatedProduct.barcode).isEqualTo("3213121")

        assertThat(updatedProduct.uid).isEqualTo(productId)
    }

    @Test
    fun getById() {
        val result = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        )
        val id = result.getDataOnSuccessOrNull()!!.uid
        assertThat(id).isNotNull()

        assertThat(
            productRepository.getById(id!!).getDataOnSuccessOrNull()
        ).isEqualTo(result.getDataOnSuccessOrNull())
    }

    @Test
    fun getAll() {
        productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        )

        productRepository.save(
            ProductCreationDTO(
                name = "qwerty",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        )

        val allProductsResult = productRepository.getAll().getDataOnSuccessOrNull()
        assertThat(allProductsResult).isNotNull()

        assertThat(allProductsResult!!.map { it.toProductCreationDTO() }).containsExactly(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            ),
            ProductCreationDTO(
                name = "qwerty",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        )
    }

    @Test
    fun autoIncrementIdWorks() {
        val product1_id = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        ).getDataOnSuccessOrNull()!!.uid

        val product2_id = productRepository.save(
            ProductCreationDTO(
                name = "qwerty",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        ).getDataOnSuccessOrNull()!!.uid

        assertThat(product1_id).isEqualTo(product2_id!! - 1)
    }

    @Test
    fun deleteById() {
        val id = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0,
                providerPrice = null,
                availableQuantity = 10
            )
        ).getDataOnSuccessOrNull()!!.uid!!

        assertThat(productRepository.getById(id).getDataOnSuccessOrNull()).isNotNull()
        productRepository.deleteById(id)
        val getAfterDeletion = productRepository.getById(id)
        assertThat(getAfterDeletion).isInstanceOf(RepositoryResponse.Failure::class)
        val exception = (getAfterDeletion as RepositoryResponse.Failure).exception
        assertThat(exception).isInstanceOf(DatabaseException.NoSuchElementFoundException::class)
        assertThat(exception.message!!).contains("Couldn't find product with id")
    }
}