package io.drullar.inventar.unit.persistence

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.containsOnly
import assertk.assertions.extracting
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import assertk.assertions.key
import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.persistence.repositories.impl.ProductsRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.persistence.DatabaseException
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.unit.utils.Factory.createOrder
import io.drullar.inventar.unit.utils.Factory.createProduct
import org.junit.After
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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
        val product =
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

    @Test
    fun getProductSoldAmount() {
        val product1 = createProduct(uid = 1)
        val product2 = createProduct(uid = 2)
        productRepository.save(product1.toProductCreationDTO())
        productRepository.save(product2.toProductCreationDTO())
        listOf(
            createOrder(
                creationDate = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).plusDays(10),
                productToQuantity = mapOf(product1 to 2, product2 to 10),
                status = OrderStatus.COMPLETED
            ),
            createOrder(
                creationDate = LocalDateTime.now(),
                productToQuantity = mapOf(product1 to 3),
                status = OrderStatus.COMPLETED
            )
        ).forEach {
            orderRepository.save(it.toOrderCreationDTO()).getOrThrow()
        }

        val search1 =
            orderRepository.getProductSoldAmount(product1.uid, null, null).getOrThrow()
        assertThat(search1.soldQuantity).isEqualTo(5)

        val search2 =
            orderRepository.getProductSoldAmount(
                product1.uid,
                LocalDate.ofInstant(Instant.now().minusSeconds(86400), ZoneId.systemDefault()),
                null
            ).getOrThrow()
        assertThat(search2.soldQuantity).isEqualTo(3)
    }

    @Test
    fun getMostSoldProducts() {
        val product1 = productRepository.save(createProduct(1).toProductCreationDTO()).getOrThrow()
        val product2 = productRepository.save(createProduct(2).toProductCreationDTO()).getOrThrow()
        val product3 = productRepository.save(createProduct(3).toProductCreationDTO()).getOrThrow()

        listOf(
            createOrder(productToQuantity = mapOf(product1 to 10), status = OrderStatus.COMPLETED),
            createOrder(productToQuantity = mapOf(product3 to 20), status = OrderStatus.COMPLETED),
            createOrder(
                productToQuantity = mapOf(product1 to 15, product2 to 21),
                status = OrderStatus.COMPLETED
            )
        ).forEach {
            orderRepository.save(it.toOrderCreationDTO()).getOrThrow()
        }

        val search1 = orderRepository.getMostSoldProducts(2, null, null).getOrThrow()

        assertThat(search1).extracting { it.productId to it.soldQuantity }.containsOnly(
            1 to 25, 2 to 21
        )

        orderRepository.save(
            OrderCreationDTO(
                productToQuantity = mapOf(product3 to 10),
                status = OrderStatus.COMPLETED
            )
        ).getOrThrow()

        val search2 = orderRepository.getMostSoldProducts(2, null, null).getOrThrow()
        assertThat(search2).extracting { it.productId to it.soldQuantity }.containsOnly(
            1 to 25, 3 to 30
        )
    }

    @Test
    fun productSellingPriceDoesNotChangeOnCompletion() {
        val product = productRepository.save(
            createProduct(sellingPrice = 3.0.toBigDecimal()).toProductCreationDTO()
        ).getOrThrow()

        val order =
            orderRepository.save(
                createOrder(productToQuantity = mapOf(product to 1))
                    .toOrderCreationDTO()
            ).getOrThrow()

        orderRepository.update(
            order!!.orderId,
            order.copy(status = OrderStatus.COMPLETED).toOrderCreationDTO()
        )

        productRepository.update(
            product.uid,
            product.copy(sellingPrice = 4.0.toBigDecimal()).toProductCreationDTO()
        )

        val orderAfterUpdate = orderRepository.getById(order.orderId).getOrThrow()
        val completedOrderProduct = orderAfterUpdate.productToQuantity.keys.first()
        assertThat(completedOrderProduct.sellingPrice.toDouble()).isEqualTo(3.0)
        assertThat(orderAfterUpdate.getTotalPrice().toDouble()).isEqualTo(3.0)
    }

    @Test
    fun orderHasNoChangeWhenProductIsDelete() {
        val product = productRepository.save(createProduct().toProductCreationDTO()).getOrThrow()
        val orderCreationDTO = createOrder().toOrderCreationDTO().copy(
            productToQuantity = mapOf(product to 1)
        )
        val order = orderRepository.save(orderCreationDTO).getOrThrow()!!
        assertThat(order.productToQuantity).key(product).isEqualTo(1)

        productRepository.deleteById(product.uid).getOrThrow()
        assertThat(productRepository.getById(product.uid).getOrNull()).isEqualTo(null)

        val orderRe = orderRepository.getById(order.orderId).getOrThrow()
        assertThat(orderRe.productToQuantity).key(product).isEqualTo(1)
    }
}