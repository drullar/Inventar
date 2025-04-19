package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.Page
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.viewmodel.delegate.OrdersDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OrdersDelegateImpl : OrdersDelegate {

    private val draftOrdersCount by lazy {
        MutableStateFlow(OrderRepository.getCountByStatus(OrderStatus.DRAFT))
    }

    override fun terminateOrder(order: OrderDTO): OrderDTO {
        return changeOrderState(order, OrderStatus.TERMINATED)
    }

    override fun createOrder(products: Map<ProductDTO, Int>): OrderDTO {
        return OrderRepository.save(
            OrderCreationDTO(
                status = OrderStatus.DRAFT,
                productToQuantity = products
            )
        ).getOrThrow().also {
            draftOrdersCount.value += 1
        }
    }

    override fun completeOrder(order: OrderDTO): OrderDTO {
        return changeOrderState(order, OrderStatus.COMPLETED)
    }

    override fun getDraftOrdersCount(): StateFlow<Long> {
        return draftOrdersCount
    }

    override fun fetchOrders(pagedRequest: PagedRequest<OrderRepository.OrderSortBy>): Page<OrderDTO> =
        OrderRepository.getPaged(pagedRequest).getOrThrow()

    override fun getAllByStatus(status: OrderStatus): List<OrderDTO> {
        return OrderRepository.getAllByStatus(status).getOrThrow()
    }

    override fun updateProductsQuantity(
        order: OrderDTO,
        products: Map<ProductDTO, Int>
    ): OrderDTO {
        val orderCreationDTO = order.toOrderCreationDTO().copy(
            productToQuantity = order.productToQuantity.toMutableMap().also {
                products.forEach { entry ->
                    val product = entry.key
                    val quantity = entry.value
                    if (quantity > 0) it[product] = quantity
                    else it.remove(product)
                }
            }
        )
        return OrderRepository.update(order.orderId, orderCreationDTO).getOrThrow()
    }

    private fun changeOrderState(order: OrderDTO, status: OrderStatus): OrderDTO {
        val updatedOrder = OrderRepository.update(
            order.orderId,
            order.toOrderCreationDTO().copy(status = status)
        ).getOrThrow()
        draftOrdersCount.value -= 1
        return updatedOrder
    }
}