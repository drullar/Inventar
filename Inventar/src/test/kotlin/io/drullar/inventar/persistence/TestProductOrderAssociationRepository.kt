package io.drullar.inventar.persistence

import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.persistence.repositories.ProductOrderAssociationRepository
import io.drullar.inventar.persistence.repositories.ProductsService
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class TestProductOrderAssociationRepository : AbstractPersistenceTest() {

    private val repository = ProductOrderAssociationRepository
    private val ordersRepository = OrderRepository
    private val productsRepository = ProductsService

    @BeforeEach
    override fun cleanUp() {
        repository.deleteAll()
        ordersRepository.deleteAll()
        productsRepository.deleteAll()
    }

    @Test
    fun create() {
//        val orderId = ordersRepository.save(Order(LocalDateTime.now(), OrderStatus.DRAFT, 10.0))
//        val productId = productsRepository.save(Product(1))
//
//        repository.save(
//            ProductOrderAssociationModel(
//                productOrderKey = ProductOrderKey(productId, orderId),
//                orderedAmount = 1,
//                productSellingPrice = 0.0,
//                productName = "asdfs"
//            )
//        )
        println()
    }
}