package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.SortingOrder
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.viewmodel.delegates.SettingsProvider
import io.drullar.inventar.ui.viewmodel.delegates.SharedAppStateDelegate
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

    private val sortBy by lazy { MutableStateFlow(OrderRepository.SortBy.CREATION_DATE) }
    val _sortBy by lazy { sortBy.asStateFlow() }

    fun sortOrdersBy(sortBy: OrderRepository.SortBy) {
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

    fun fetchOrders(page: Int, pageSize: Int, sortBy: OrderRepository.SortBy, order: SortingOrder) =
        ordersRepository.getPaged(page, pageSize, sortBy, order)
}