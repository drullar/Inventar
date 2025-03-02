package io.drullar.inventar.ui.components.views.order

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import io.drullar.inventar.ui.components.cards.SimpleOrderRow
import io.drullar.inventar.ui.components.viewmodel.OrderViewViewModel

@Composable
fun OrdersView(ordersViewViewModel: OrderViewViewModel) {
    LazyColumn {
        items(items = ordersViewViewModel._orders.value ?: emptyList()) { item ->
            SimpleOrderRow(item, {}, {}, true)
        }
    }
}