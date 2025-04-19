package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.viewmodel.delegate.OrdersDelegate
import io.drullar.inventar.ui.viewmodel.delegate.SettingsProvider
import io.drullar.inventar.ui.viewmodel.delegate.SharedAppStateDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewViewModel(
    sharedAppStateDelegate: SharedAppStateDelegate,
    settingsProvider: SettingsProvider,
    private val ordersDelegate: OrdersDelegate,
) : SharedAppStateDelegate by sharedAppStateDelegate,
    SettingsProvider by settingsProvider,
    OrdersDelegate by ordersDelegate {

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

    override fun createOrder(products: Map<ProductDTO, Int>): OrderDTO {
        val newOrder = ordersDelegate.createOrder(products)
        setPreview(OrderDetailsPreview(newOrder))
        setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
        return newOrder
    }
}