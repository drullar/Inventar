package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.SortingOrder
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.getDataOnSuccessOrNull
import io.drullar.inventar.shared.getOrThrow
import io.drullar.inventar.sortedBy
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.viewmodel.delegates.SettingsProvider
import io.drullar.inventar.ui.viewmodel.delegates.SharedAppStateDelegate
import io.drullar.inventar.ui.viewmodel.delegates.getText
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

    private val orderBy by lazy {
        MutableStateFlow(OrderBy.DATE) // getAll by default sorts by this
    }
    val _orderBy by lazy { orderBy.asStateFlow() }

    private val lastFetchedPage by lazy {
        MutableStateFlow(ordersRepository.getAllPaged(1, 20).getDataOnSuccessOrNull())
    }

    // TODO fix pagination not showing all orders
    private val orders by lazy {
        MutableStateFlow(
            lastFetchedPage.value?.items ?: emptyList()
        )
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

    fun loadNextOrdersPage() {
        lastFetchedPage.value =
            ordersRepository.getAllPaged(
                page = (lastFetchedPage.value?.pageNumber ?: 0) + 1,
                itemsPerPage = ordersFetchSize
            ).getDataOnSuccessOrNull()

        lastFetchedPage.value?.items?.let { newFetchedOrders ->
            orders.value += newFetchedOrders
        }
    }

    fun newOrder() {
        val newOrder = ordersRepository.save(OrderCreationDTO(emptyMap(), OrderStatus.DRAFT))
            .getOrThrow()

        setPreview(OrderDetailsPreview(newOrder))
        setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
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

    enum class OrderBy(val text: String) {
        DATE(getText("field.date")),
        ID("field.number"),
        TOTAL_PRICE("field.total.price"),
        STATUS("field.status")
    }
}