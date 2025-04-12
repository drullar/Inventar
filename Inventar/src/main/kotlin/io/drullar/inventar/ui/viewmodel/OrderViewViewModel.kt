package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.viewmodel.delegate.SettingsProvider
import io.drullar.inventar.ui.viewmodel.delegate.SharedAppStateDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewViewModel(
    sharedAppStateDelegate: SharedAppStateDelegate,
    settingsProvider: SettingsProvider,
    private val ordersRepository: OrderRepository = OrderRepository
) : SharedAppStateDelegate by sharedAppStateDelegate,
    SettingsProvider by settingsProvider {

    private val sortingOrder by lazy {
        MutableStateFlow(SortingOrder.ASCENDING)
    }
    val _sortingOrder by lazy { sortingOrder.asStateFlow() }

    private val sortBy by lazy { MutableStateFlow(OrderRepository.OrderSortBy.CREATION_DATE) }
    val _sortBy by lazy { sortBy.asStateFlow() }

    fun sortOrdersBy(sortBy: OrderRepository.OrderSortBy) {
        this.sortBy.value = sortBy
    }

    fun setSortingOrder(sortingOrder: SortingOrder) {
        this.sortingOrder.value = sortingOrder
    }

    fun newOrder() {
        val newOrder = ordersRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
            .getOrThrow()!!

        setPreview(OrderDetailsPreview(newOrder))
        setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
    }

    fun fetchOrders(pagedRequest: PagedRequest<OrderRepository.OrderSortBy>) =
        ordersRepository.getPaged(pagedRequest)

    fun changeOrderStatus(order: OrderDTO, status: OrderStatus) =
        ordersRepository.update(order.orderId, order.copy(status = status).toOrderCreationDTO())
}