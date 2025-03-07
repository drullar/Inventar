package io.drullar.inventar.ui.components.viewmodel

import io.drullar.inventar.SortingOrder
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.persistence.repositories.OrderRepository.OrderBy
import io.drullar.inventar.shared.getDataOnSuccessOrNull
import io.drullar.inventar.sortedBy
import io.drullar.inventar.ui.components.viewmodel.delegates.SharedAppStateDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewViewModel(
    sharedAppStateDelegate: SharedAppStateDelegate,
    private val ordersRepository: OrderRepository = OrderRepository
) : SharedAppStateDelegate by sharedAppStateDelegate {

    private val sortingOrder by lazy {
        MutableStateFlow(SortingOrder.ASCENDING)
    }
    val _sortingOrder by lazy { sortingOrder.asStateFlow() }

    private val orderBy by lazy {
        MutableStateFlow(OrderBy.DATE) // getAll by default sorts by this
    }
    val _orderBy by lazy { orderBy.asStateFlow() }

    private val orders by lazy {
        MutableStateFlow(ordersRepository.getAll().getDataOnSuccessOrNull() ?: emptyList())
    }
    val _orders by lazy { orders.asStateFlow() }

    fun orderOrdersBy(orderBy: OrderBy) {
        this.orderBy.value = orderBy
        sortOrders()
    }

    fun setSortingOrder(sortingOrder: SortingOrder) {
        if (sortingOrder != this.sortingOrder.value) {
            this.sortingOrder.value = sortingOrder
            sortOrders()
        }
    }

    private fun sortOrders() {
        when (orderBy.value) {
            OrderBy.STATUS -> {
                orders.value = orders.value.sortedBy(sortingOrder.value) { it.status }
            }

            OrderBy.ID -> {
                orders.value = orders.value.sortedBy(sortingOrder.value) { it.orderId }
            }

            OrderBy.DATE -> {
                orders.value = orders.value.sortedBy(sortingOrder.value) { it.creationDate }
            }

            OrderBy.TOTAL_PRICE -> {
                orders.value = orders.value.sortedBy(sortingOrder.value) { it.getTotalPrice() }
            }
        }
    }
}