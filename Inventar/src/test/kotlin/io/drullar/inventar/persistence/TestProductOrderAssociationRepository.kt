package io.drullar.inventar.persistence

import io.drullar.inventar.persistence.model.Order
import io.drullar.inventar.persistence.model.OrderStatus
import io.drullar.inventar.persistence.model.Product
import io.drullar.inventar.persistence.model.ProductOrderAssociationModel
import io.drullar.inventar.persistence.model.id.ProductOrderKey
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.persistence.repositories.ProductOrderAssociationRepository
import io.drullar.inventar.persistence.repositories.ProductsRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import kotlin.test.Test

class TestProductOrderAssociationRepository : AbstractPersistenceTest() {

    private val repository = ProductOrderAssociationRepository
    private val ordersRepository = OrderRepository
    private val productsRepository = ProductsRepository

    @BeforeEach
    override fun cleanUp() {
        repository.deleteAll()
        ordersRepository.deleteAll()
        productsRepository.deleteAll()
    }

    @Test
    fun create() {
        val orderId = ordersRepository.save(Order(LocalDateTime.now(), OrderStatus.DRAFT, 10.0))
        val productId = productsRepository.save(Product("Product"))

        repository.save(
            ProductOrderAssociationModel(
                productOrderKey = ProductOrderKey(productId, orderId),
                orderedAmount = 1,
                productSellingPrice = 0.0,
                productName = "asdfs"
            )
        )
        println()
    }
}