package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.SortingOrder
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.sortedBy
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

    private val ordersFetchSize = 20

    private val sortingOrder by lazy {
        MutableStateFlow(SortingOrder.ASCENDING)
    }
    val _sortingOrder by lazy { sortingOrder.asStateFlow() }

    private val sortBy by lazy { MutableStateFlow(OrderRepository.SortBy.CREATION_DATE) }
    val _sortBy by lazy { sortBy.asStateFlow() }

    private val lastFetchedPage by lazy {
        MutableStateFlow(
            ordersRepository.getPaged(
                1,
                ordersFetchSize,
                sortBy.value,
                sortingOrder.value
            ).getOrNull()
        )
    }

    // TODO fix pagination not showing all orders
    private val orders by lazy {
        MutableStateFlow(
            lastFetchedPage.value?.items ?: emptyList()
        )
    }
    val _orders by lazy { orders.asStateFlow() }

    fun sortOrdersBy(sortBy: OrderRepository.SortBy) {
        this.sortBy.value = sortBy
        sortOrders()
    }

    fun setSortingOrder(sortingOrder: SortingOrder) {
        if (sortingOrder != this.sortingOrder.value) {
            this.sortingOrder.value = sortingOrder
            sortOrders()
        }
    }

    fun loadNextOrdersPage() {
        lastFetchedPage.value =
            ordersRepository.getPaged(
                page = (lastFetchedPage.value?.pageNumber ?: 0) + 1,
                itemsPerPage = ordersFetchSize,
                sortBy = sortBy.value,
                order = sortingOrder.value
            ).getOrNull()

        lastFetchedPage.value?.items?.let { newFetchedOrders ->
            orders.value += newFetchedOrders
        }
    }

    fun newOrder() {
        val newOrder = ordersRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
            .getOrThrow()!!

        setPreview(OrderDetailsPreview(newOrder))
        setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
    }

    private fun sortOrders() {
        when (sortBy.value) {
            OrderRepository.SortBy.STATUS -> {
                orders.value = orders.value.sortedBy(sortingOrder.value) { it.status }
            }

            OrderRepository.SortBy.NUMBER -> {
                orders.value = orders.value.sortedBy(sortingOrder.value) { it.orderId }
            }

            OrderRepository.SortBy.CREATION_DATE -> {
                orders.value = orders.value.sortedBy(sortingOrder.value) { it.creationDate }
            }

//            OrderBy.TOTAL_PRICE -> {
//                orders.value = orders.value.sortedBy(sortingOrder.value) { it.getTotalPrice() }
//            }
        }
    }
}