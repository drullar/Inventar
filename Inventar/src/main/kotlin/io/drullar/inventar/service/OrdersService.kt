package io.drullar.inventar.service

import io.drullar.inventar.persistence.model.Order
import io.drullar.inventar.persistence.model.OrderStatus
import io.drullar.inventar.persistence.repositories.OrderRepository
import java.time.LocalDateTime

class OrdersService {
    private val orderRepository = OrderRepository
    private val productsService by lazy { ProductsService() }

//    fun createDraft(request: SaveOrderRequest) {
//        val products = request.productIdToQuantity.mapNotNull {
//            productsService.getProductById(it.key)
//        }
//        val totalPrice = products.sumOf { it.sellingPrice }
//        orderRepository.save(
//            Order(
//                creationDate = LocalDateTime.now(),
//                status = OrderStatus.DRAFT,
//                totalPrice
//            )
//        )
//    }

    fun completeOrder(orderId: String) {

    }

    fun getDraftOrders() = orderRepository.getAllByStatus(OrderStatus.DRAFT)

    fun modifyDraft() {}
}