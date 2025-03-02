package io.drullar.inventar.ui.components.viewmodel

import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.shared.getDataOnSuccessOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewViewModel(
    sharedAppStateHolder: SharedAppStateHolder,
    private val ordersRepository: OrderRepository = OrderRepository
) : SharedAppStateHolder by sharedAppStateHolder {

    private val orders by lazy { MutableStateFlow(getAllOrders()) }
    val _orders by lazy { orders.asStateFlow() }

    private fun getAllOrders() = ordersRepository.getAll().getDataOnSuccessOrNull()
}