package io.drullar.inventar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.dialog.SingleActionAlertDialog
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.views.order.OrdersView
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.DefaultView
import io.drullar.inventar.ui.components.search.SearchBar
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.viewmodel.delegates.AlertManager
import io.drullar.inventar.ui.viewmodel.delegates.SharedAppStateDelegate
import io.drullar.inventar.ui.data.AlertType

@Composable
fun App(
    sharedAppState: SharedAppStateDelegate,
    alertManager: AlertManager,
    defaultViewViewModel: DefaultViewViewModel,
    orderViewViewModel: OrderViewViewModel
) {
    val currentView = sharedAppState.getNavigationDestination().collectAsState()
    val activeAlert = alertManager.getActiveAlert().collectAsState()

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            NavigationBar(
                selectedView = currentView.value,
                modifier = Modifier.heightIn(30.dp, 40.dp).widthIn(100.dp, 300.dp),
                onNavigationChange = { sharedAppState.setNavigationDestination(it) }
            )

            SearchBar(
                modifier = Modifier.heightIn(30.dp, 40.dp).fillMaxWidth(0.5f),
                onSearchSubmit = { /*TODO search implementation*/ })
        }

        val viewModifier = Modifier.fillMaxWidth().fillMaxHeight()
        when (currentView.value) {
            NavigationDestination.PRODUCTS_PAGE -> {
                DefaultView(defaultViewViewModel, viewModifier)
            }

            NavigationDestination.ORDERS_PAGE -> {
                OrdersView(orderViewViewModel)
            }
        }

        when (activeAlert.value) {
            AlertType.UNSAVED_CHANGES -> {
                SingleActionAlertDialog(
                    text = "You have unsaved changes. Save or revert the changes to proceed with this action",
                    actionButtonText = "Acknowledge"
                ) {
                    alertManager.setActiveAlert(null)
                }
            }

            else -> {}
        }
    }
}