package io.drullar.inventar.ui.viewmodel.delegate

import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.ProductDTO
import kotlinx.coroutines.flow.StateFlow

interface OrdersDelegate {
    fun terminateOrder(order: OrderDTO): OrderDTO
    fun completeOrder(order: OrderDTO): OrderDTO
    fun createOrder(products: Map<ProductDTO, Int>): OrderDTO
    fun getDraftOrdersCount(): StateFlow<Long>
    fun fetchOrders(pagedRequest: PagedRequest<OrderRepository.OrderSortBy>): Page<OrderDTO>
    fun getAllByStatus(status: OrderStatus): List<OrderDTO>
    fun updateProductsQuantity(order: OrderDTO, products: Map<ProductDTO, Int>): OrderDTO
}