package io.drullar.inventar.unit.persistence

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.containsOnly
import assertk.assertions.extracting
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isSuccess
import io.drullar.inventar.persistence.repositories.impl.ProductsRepository
import io.drullar.inventar.persistence.schema.Products
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.unit.utils.Factory.createProduct
import org.junit.After
import org.junit.Test
import java.math.BigDecimal

class TestProductRepository : AbstractPersistenceTest() {

    private val productRepository = ProductsRepository

    @After
    fun cleanup() {
        productRepository.deleteAll()
        val table = Products.autoIncColumn!!.table
    }

    @Test
    fun save() {
        val result = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        )
        assertThat(result).isSuccess()

        val data = result.getOrNull()
        assertThat(data).isNotNull()
        assertThat(data!!.name).isEqualTo("asdfs")
        assertThat(data.availableQuantity).isEqualTo(10)
        assertThat(data.barcode).isEqualTo("021321321")
        assertThat(data.sellingPrice).isEqualTo(BigDecimal("0.00"))
        assertThat(data.providerPrice).isNull()
        assertThat(data.uid).isNotNull()
    }

    @Test
    fun update() {
        val createResult = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        )
        assertThat(createResult).isSuccess()

        val data = createResult.getOrNull()
        assertThat(data).isNotNull()
        val productId = data!!.uid
        assertThat(productId).isNotNull()

        val updateResult =
            productRepository.update(
                productId!!,
                data.toProductCreationDTO().copy(
                    name = "new name",
                    sellingPrice = 2.0.toBigDecimal(),
                    providerPrice = 0.0.toBigDecimal(),
                    barcode = "3213121"
                )
            )

        val updatedProduct = updateResult.getOrNull()
        assertThat(updatedProduct).isNotNull()
        assertThat(updatedProduct!!.name).isEqualTo("new name")
        assertThat(updatedProduct.sellingPrice).isEqualTo("2.00".toBigDecimal())
        assertThat(updatedProduct.providerPrice).isEqualTo("0.00".toBigDecimal())
        assertThat(updatedProduct.barcode).isEqualTo("3213121")

        assertThat(updatedProduct.uid).isEqualTo(productId)
    }

    @Test
    fun getById() {
        val result = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        )
        val id = result.getOrNull()!!.uid
        assertThat(id).isNotNull()

        assertThat(
            productRepository.getById(id!!).getOrNull()
        ).isEqualTo(result.getOrNull())
    }

    @Test
    fun getAll() {
        productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        )

        productRepository.save(
            ProductCreationDTO(
                name = "qwerty",
                barcode = "021321321",
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        )

        val allProductsResult = productRepository.getAll().getOrNull()
        assertThat(allProductsResult).isNotNull()

        assertThat(allProductsResult!!.map { it.toProductCreationDTO() }).containsExactly(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = "0.00".toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            ),
            ProductCreationDTO(
                name = "qwerty",
                barcode = "021321321",
                sellingPrice = "0.00".toBigDecimal(),
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
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        ).getOrThrow()

        val product2_id = productRepository.save(
            ProductCreationDTO(
                name = "qwerty",
                barcode = "021321321",
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        ).getOrThrow()

        assertThat(product1_id.uid).isEqualTo(product2_id.uid - 1)
    }

    @Test
    fun deleteById() {
        val id = productRepository.save(
            ProductCreationDTO(
                name = "asdfs",
                barcode = "021321321",
                sellingPrice = 0.0.toBigDecimal(),
                providerPrice = null,
                availableQuantity = 10
            )
        ).getOrNull()!!.uid!!

        assertThat(productRepository.getById(id).getOrNull()).isNotNull()
        productRepository.deleteById(id)
        val getAfterDeletion = productRepository.getById(id)
        assertThat(getAfterDeletion).isFailure()
        val exception = getAfterDeletion.exceptionOrNull()!!
        assertThat(exception::class).isEqualTo(DatabaseException.NoSuchElementFoundException::class)
        assertThat(exception!!.message!!).contains("Couldn't find product with id")
    }

    @Test
    fun search() {
        val barcode = "9234-564534"
        val products = listOf(
            createProduct(name = "Loreal Paris"),
            createProduct(name = "Cola Coca"),
            createProduct(name = "Derby cola"),
            createProduct(name = "Mark Loreal", barcode = barcode),
            createProduct(name = "CocktaCola")
        ).mapIndexed { index, item -> item.copy(uid = index) }

        products.forEach { productRepository.save(it.toProductCreationDTO()) }

        val lorealProducts = productRepository.search(
            "loreal",
            PagedRequest(
                1,
                10,
                order = SortingOrder.ASCENDING,
                sortBy = ProductsRepository.SortBy.ID
            )
        ).getOrThrow().items

        assertThat(lorealProducts)
            .extracting { it.name }
            .containsOnly(
                "Loreal Paris",
                "Mark Loreal"
            )

        val colaProducts = productRepository.search(
            "COLA",
            PagedRequest(
                1,
                10,
                order = SortingOrder.ASCENDING,
                sortBy = ProductsRepository.SortBy.ID
            )
        ).getOrThrow().items

        assertThat(colaProducts)
            .extracting { it.name }
            .containsOnly(
                "Cola Coca",
                "Derby cola",
                "CocktaCola"
            )

        val barcodeSearch = productRepository.search(
            barcode,
            PagedRequest(
                1,
                10,
                order = SortingOrder.ASCENDING,
                sortBy = ProductsRepository.SortBy.ID
            )
        ).getOrThrow().items

        assertThat(barcodeSearch)
            .extracting { it.name }
            .containsOnly("Mark Loreal")

        val pagedSearch1 = productRepository.search(
            "COLA",
            PagedRequest(
                1,
                1,
                order = SortingOrder.ASCENDING,
                sortBy = ProductsRepository.SortBy.ID
            )
        ).getOrThrow()

        val pagedSearch2 = productRepository.search(
            "COLA",
            PagedRequest(
                2,
                1,
                order = SortingOrder.ASCENDING,
                sortBy = ProductsRepository.SortBy.ID
            )
        ).getOrThrow()

        assertThat(pagedSearch1.isLastPage).isFalse()
        assertThat(pagedSearch1.totalItems).isEqualTo(3)
        assertThat(pagedSearch2.isLastPage).isFalse()

        assertThat(pagedSearch1.items).extracting { it.name }.containsOnly("Cola Coca")
        assertThat(pagedSearch2.items).extracting { it.name }.containsOnly("Derby cola")

        val searchById = productRepository.search(
            "1",
            PagedRequest(
                1,
                10,
                order = SortingOrder.ASCENDING,
                sortBy = ProductsRepository.SortBy.ID
            )
        ).getOrThrow().items

        assertThat(searchById).extracting { it.name }.containsOnly("Loreal Paris")
    }
}