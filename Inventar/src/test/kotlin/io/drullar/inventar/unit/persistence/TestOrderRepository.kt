package io.drullar.inventar.unit.persistence

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.extracting
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.persistence.repositories.ProductsRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.RepositoryResponse
import io.drullar.inventar.shared.getDataOnSuccessOrNull
import io.drullar.inventar.persistence.DatabaseException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TestOrderRepository : AbstractPersistenceTest() {

    private val productRepository = ProductsRepository
    private val orderRepository = OrderRepository

    @AfterEach
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
        ).getDataOnSuccessOrNull()

        assertThat(result).isNotNull()
        assertThat(result!!.status).isEqualTo(OrderStatus.DRAFT)

        val completedOrderWithoutProducts = orderRepository.save(
            OrderCreationDTO(
                productToQuantity = emptyMap(),
                status = OrderStatus.COMPLETED
            )
        )

        assertThat(completedOrderWithoutProducts).isInstanceOf(RepositoryResponse.Failure::class)
        val exception = (completedOrderWithoutProducts as RepositoryResponse.Failure).exception
        assertThat(exception).isInstanceOf(DatabaseException.InvalidOperationException::class)
        assertThat(exception.message).isEqualTo("Can not save an order without selected products.")
    }

    @Test
    fun saveWithProducts() {
        val products = mutableMapOf<ProductDTO, Int>()
        for (i in 0..10) {
            val product =
                productRepository.save(ProductCreationDTO("Product$i")).getDataOnSuccessOrNull()
            assertThat(product).isNotNull()
            products.put(product!!, i + 1)
        }

        val result = orderRepository.save(
            OrderCreationDTO(
                productToQuantity = products,
                status = OrderStatus.DRAFT,
            )
        ).getDataOnSuccessOrNull()

        assertThat(result).isNotNull()
        assertThat(result!!.status).isEqualTo(OrderStatus.DRAFT)
        assertThat(result.productToQuantity).isEqualTo(products)
    }

    @Test
    fun update() {
        val products = listOf(
            productRepository.save(ProductCreationDTO("Product1", availableQuantity = 30))
                .getDataOnSuccessOrNull()!!,
            productRepository.save(ProductCreationDTO("Product2", availableQuantity = 30))
                .getDataOnSuccessOrNull()!!,
            productRepository.save(ProductCreationDTO("Product3", availableQuantity = 30))
                .getDataOnSuccessOrNull()!!
        )

        val productsQuantityAssociation = products.withIndex().associate {
            it.value to it.index + 1
        }.toMutableMap()

        val initialOrderSave = orderRepository.save(
            OrderCreationDTO(
                productToQuantity = emptyMap(),
                status = OrderStatus.DRAFT
            )
        ).getDataOnSuccessOrNull()

        assertThat(initialOrderSave).isNotNull()
        assertThat(initialOrderSave!!.productToQuantity).isEmpty()

        val updatedOrderWithProducts = orderRepository.update(
            initialOrderSave.orderId,
            initialOrderSave.toOrderCreationDTO()
                .copy(productToQuantity = productsQuantityAssociation)
        ).getDataOnSuccessOrNull()

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
        ).getDataOnSuccessOrNull()

        assertThat(updatedAndCompletedOrder).isNotNull()
        assertThat(updatedAndCompletedOrder!!.orderId).isEqualTo(initialOrderSave.orderId)
        assertThat(updatedAndCompletedOrder.status).isEqualTo(OrderStatus.COMPLETED)
        assertThat(updatedAndCompletedOrder.productToQuantity.size).isEqualTo(2)
        assertThat(updatedAndCompletedOrder.productToQuantity.keys).extracting { it.uid }
            .isEqualTo(productsQuantityAssociation.keys.map { it.uid })

        // Validate association table consistency
        val associatedProducts =
            orderRepository.getAllProductsAssociatedWithOrder(initialOrderSave.orderId)
                .getDataOnSuccessOrNull()

        assertThat(associatedProducts!!.size).isEqualTo(2)
        assertThat(associatedProducts).extracting { it.uid }
            .containsExactlyInAnyOrder(products[0].uid, products[1].uid)

        val failedUpdateOfCompletedOrder = orderRepository.update(
            initialOrderSave.orderId,
            updatedAndCompletedOrder.toOrderCreationDTO()
        )

        assertThat(failedUpdateOfCompletedOrder).isInstanceOf(RepositoryResponse.Failure::class)
        val exception = (failedUpdateOfCompletedOrder as RepositoryResponse.Failure).exception
        assertThat(exception.message).isEqualTo("Can not update order which has already been completed")
    }

    @Test
    fun getAllByStatus() {
        var product =
            productRepository.save(ProductCreationDTO("Product", availableQuantity = 1))
                .getDataOnSuccessOrNull()!!

        val draftOrders = listOf(
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
                .getDataOnSuccessOrNull(),
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
                .getDataOnSuccessOrNull()
        )

        val canceledOrder =
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.TERMINATED))
                .getDataOnSuccessOrNull()

        val completedOrder =
            orderRepository.save(OrderCreationDTO(mapOf(product to 1), OrderStatus.COMPLETED))
                .getDataOnSuccessOrNull()
        product = productRepository.getById(product.uid).getDataOnSuccessOrNull()!!

        val getAllDraft = orderRepository.getAllByStatus(OrderStatus.DRAFT).getDataOnSuccessOrNull()
        val getAllCanceled =
            orderRepository.getAllByStatus(OrderStatus.TERMINATED).getDataOnSuccessOrNull()
        val getAllCompleted =
            orderRepository.getAllByStatus(OrderStatus.COMPLETED).getDataOnSuccessOrNull()

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

        assertThat(orderRepository.getAll().getDataOnSuccessOrNull()!!.size).isEqualTo(10)
    }

    @Test
    fun deleteAll() {
        repeat(10) {
            orderRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
        }

        assertThat(orderRepository.getAll().getDataOnSuccessOrNull()!!.size).isEqualTo(10)
        orderRepository.deleteAll()
        assertThat(orderRepository.getAll().getDataOnSuccessOrNull()!!.size).isEqualTo(0)
    }

}