package io.drullar.inventar.unit.persistence

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.extracting
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import io.drullar.inventar.SortingOrder
import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.persistence.repositories.impl.ProductsRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.PagedRequest
import org.junit.After
import org.junit.Test

class TestOrderRepository : AbstractPersistenceTest() {

    private val productRepository = ProductsRepository
    private val orderRepository = OrderRepository

    @After
    fun cleanUp() {
        orderRepository.deleteAll()
    }

    @Test
    fun saveWithoutProducts() {
        val result = orderRepository.save(
            OrderCreationDTO(
                productToQuantity = emptyMap(),
                status = OrderStatus.DRAFT
            )
        ).getOrNull()

        assertThat(result).isNotNull()
        assertThat(result!!.status).isEqualTo(OrderStatus.DRAFT)

        val completedOrderWithoutProducts = orderRepository.save(
            OrderCreationDTO(
                productToQuantity = emptyMap(),
                status = OrderStatus.COMPLETED
            )
        )

        assertThat(completedOrderWithoutProducts).isFailure()

        val exception = completedOrderWithoutProducts.exceptionOrNull()!!
        assertThat(exception::class.java).isEqualTo(DatabaseException.InvalidOperationException::class.java)
        assertThat(exception.message).isEqualTo("Can not save an order without selected products.")
    }

    @Test
    fun saveWithProducts() {
        val products = mutableMapOf<ProductDTO, Int>()
        for (i in 0..10) {
            val product =
                productRepository.save(ProductCreationDTO("Product$i")).getOrNull()
            assertThat(product).isNotNull()
            products.put(product!!, i + 1)
        }

        val result = orderRepository.save(
            OrderCreationDTO(
                productToQuantity = products,
                status = OrderStatus.DRAFT,
            )
        ).getOrNull()

        assertThat(result).isNotNull()
        assertThat(result!!.status).isEqualTo(OrderStatus.DRAFT)
        assertThat(result.productToQuantity).isEqualTo(products)
    }

    @Test
    fun update() {
        val products = listOf(
            productRepository.save(ProductCreationDTO("Product1", availableQuantity = 30))
                .getOrNull()!!,
            productRepository.save(ProductCreationDTO("Product2", availableQuantity = 30))
                .getOrNull()!!,
            productRepository.save(ProductCreationDTO("Product3", availableQuantity = 30))
                .getOrNull()!!
        )

        val productsQuantityAssociation = products.withIndex().associate {
            it.value to it.index + 1
        }.toMutableMap()

        val initialOrderSave = orderRepository.save(
            OrderCreationDTO(
                productToQuantity = emptyMap(),
                status = OrderStatus.DRAFT
            )
        ).getOrNull()

        assertThat(initialOrderSave).isNotNull()
        assertThat(initialOrderSave!!.productToQuantity).isEmpty()

        val updatedOrderWithProducts = orderRepository.update(
            initialOrderSave.orderId,
            initialOrderSave.toOrderCreationDTO()
                .copy(productToQuantity = productsQuantityAssociation)
        ).getOrNull()

        assertThat(updatedOrderWithProducts).isNotNull()
        assertThat(updatedOrderWithProducts!!.orderId).isEqualTo(initialOrderSave.orderId)
        assertThat(updatedOrderWithProducts.productToQuantity).isEqualTo(productsQuantityAssociation)
        assertThat(updatedOrderWithProducts.status).isEqualTo(OrderStatus.DRAFT)

        productsQuantityAssociation.remove(products.last())
        productsQuantityAssociation[products.first()] = 4

        val updatedAndCompletedOrder = orderRepository.update(
            updatedOrderWithProducts.orderId,
            OrderCreationDTO(
                productToQuantity = productsQuantityAssociation,
                status = OrderStatus.COMPLETED
            )
        ).getOrNull()

        assertThat(updatedAndCompletedOrder).isNotNull()
        assertThat(updatedAndCompletedOrder!!.orderId).isEqualTo(initialOrderSave.orderId)
        assertThat(updatedAndCompletedOrder.status).isEqualTo(OrderStatus.COMPLETED)
        assertThat(updatedAndCompletedOrder.productToQuantity.size).isEqualTo(2)
        assertThat(updatedAndCompletedOrder.productToQuantity.keys).extracting { it.uid }
            .isEqualTo(productsQuantityAssociation.keys.map { it.uid })

        // Validate association table consistency
        val associatedProducts =
            orderRepository.getAllProductsAssociatedWithOrder(initialOrderSave.orderId)
                .getOrNull()

        assertThat(associatedProducts!!.size).isEqualTo(2)
        assertThat(associatedProducts).extracting { it.uid }
            .containsExactlyInAnyOrder(products[0].uid, products[1].uid)

        val failedUpdateOfCompletedOrder = orderRepository.update(
            initialOrderSave.orderId,
            updatedAndCompletedOrder.toOrderCreationDTO()
        )

        assertThat(failedUpdateOfCompletedOrder).isFailure()
        val exception = failedUpdateOfCompletedOrder.exceptionOrNull()
        assertThat(exception!!.message).isEqualTo("Can not update order which has already been completed")
    }

    @Test
    fun getAllByStatus() {
        var product =
            productRepository.save(ProductCreationDTO("Product", availableQuantity = 1))
                .getOrNull()!!

        val draftOrders = listOf(
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
                .getOrNull(),
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
                .getOrNull()
        )

        val canceledOrder =
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.TERMINATED))
                .getOrNull()

        val completedOrder =
            orderRepository.save(OrderCreationDTO(mapOf(product to 1), OrderStatus.COMPLETED))
                .getOrNull()
        product = productRepository.getById(product.uid).getOrNull()!!

        val getAllDraft = orderRepository.getAllByStatus(OrderStatus.DRAFT).getOrNull()
        val getAllCanceled =
            orderRepository.getAllByStatus(OrderStatus.TERMINATED).getOrNull()
        val getAllCompleted =
            orderRepository.getAllByStatus(OrderStatus.COMPLETED).getOrNull()

        assertThat(getAllDraft!!).extracting { it.orderId }
            .containsExactlyInAnyOrder(*draftOrders.map { it!!.orderId }.toTypedArray())

        assertThat(getAllCompleted!!).extracting { it.orderId }
            .containsExactly(completedOrder!!.orderId)

        assertThat(getAllCanceled!!).extracting { it.orderId }
            .containsExactly(canceledOrder!!.orderId)
    }

    @Test
    fun getAll() {
        repeat(10) {
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
        }

        assertThat(orderRepository.getAll().getOrNull()!!.size).isEqualTo(10)
    }

    @Test
    fun deleteAll() {
        repeat(10) {
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
        }

        assertThat(orderRepository.getAll().getOrNull()!!.size).isEqualTo(10)
        orderRepository.deleteAll()
        assertThat(orderRepository.getAll().getOrNull()!!.size).isEqualTo(0)
    }

    @Test
    fun getCount() {
        repeat(10) {
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
        }
        assertThat(orderRepository.getCount().getOrNull()).isEqualTo(10)
        orderRepository.deleteAll()
        assertThat(orderRepository.getCount().getOrNull()).isEqualTo(0)
        orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
        assertThat(orderRepository.getCount().getOrNull()).isEqualTo(1)
    }

    @Test
    fun getAllPaged() {
        repeat(40) {
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
        }

        val firstPage =
            orderRepository.getPaged(
                PagedRequest(
                    1,
                    25, SortingOrder.ASCENDING,
                    OrderRepository.OrderSortBy.CREATION_DATE,
                )
            )
                .getOrNull()
        assertAll {
            assertThat(firstPage).isNotNull()
            assertThat(firstPage!!.isLastPage).isFalse()
            assertThat(firstPage.items.size).isEqualTo(25)
            assertThat(firstPage.itemsPerPage).isEqualTo(25)
            assertThat(firstPage.pageNumber).isEqualTo(1)
            assertThat(firstPage.totalItems).isEqualTo(40)
        }

        val secondPage =
            orderRepository.getPaged(
                PagedRequest(
                    2,
                    25,
                    SortingOrder.ASCENDING,
                    OrderRepository.OrderSortBy.CREATION_DATE,
                )
            )
                .getOrNull()
        assertAll {
            assertThat(secondPage).isNotNull()
            assertThat(secondPage!!.isLastPage).isTrue()
            assertThat(secondPage.items.size).isEqualTo(15)
            assertThat(secondPage.itemsPerPage).isEqualTo(25)
            assertThat(secondPage.pageNumber).isEqualTo(2)
            assertThat(secondPage.totalItems).isEqualTo(40)
        }
    }
}