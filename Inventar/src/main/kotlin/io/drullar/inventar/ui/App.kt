package io.drullar.inventar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.ui.components.button.IconButton
import io.drullar.inventar.ui.components.dialog.SingleActionAlertDialog
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.views.order.OrdersView
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.DefaultView
import io.drullar.inventar.ui.components.search.SearchBar
import io.drullar.inventar.ui.components.views.settings.SettingsView
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.viewmodel.delegates.AlertManager
import io.drullar.inventar.ui.viewmodel.delegates.SharedAppStateDelegate
import io.drullar.inventar.ui.data.AlertType
import io.drullar.inventar.ui.provider.getAppStyle
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.SettingsViewModel
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.provider.impl.TextProviderImpl
import io.drullar.inventar.ui.provider.impl.AppStyleProviderImpl
import io.drullar.inventar.ui.style.AppStyle

@Composable
fun App(
    sharedAppState: SharedAppStateDelegate,
    alertManager: AlertManager,
    defaultViewViewModel: DefaultViewViewModel,
    orderViewViewModel: OrderViewViewModel,
    settingsViewModel: SettingsViewModel,
    windowSize: DpSize
) {
    val settingsState = settingsViewModel.getSettings().collectAsState()
    val activeLanguage = settingsState.value.language

    initializeProviders(activeLanguage, windowSize)
    println(getAppStyle())

    val currentView = sharedAppState.getNavigationDestination().collectAsState()
    val activeAlert = alertManager.getActiveAlert().collectAsState()

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(
                    0.7f
                )
            ) {
                NavigationBar(
                    selectedView = currentView.value,
                    modifier = Modifier.fillMaxWidth(if (getAppStyle() == AppStyle.NORMAL) 0.3f else 0.5f),
                    onNavigationChange = { sharedAppState.setNavigationDestination(it) }
                )

                SearchBar(
                    modifier = Modifier.heightIn(30.dp, 40.dp).fillMaxWidth(0.5f),
                    onSearchSubmit = { /*TODO search implementation*/ }
                )
            }

            IconButton(
                onClick = { sharedAppState.setNavigationDestination(NavigationDestination.SETTINGS_PAGE) },
                onHoverText = getText("label.settings"),
                buttonColors = ButtonColors(
                    Color.Transparent,
                    Color.Black,
                    Color.Transparent,
                    Color.Black
                )
            ) {
                Icon(
                    painterResource(Icons.COG_WHEEL),
                    getText("label.settings"),
                    Modifier.height(20.dp)
                )
            }
        }

        val viewModifier = Modifier.fillMaxWidth().fillMaxHeight()
        when (currentView.value) {
            NavigationDestination.PRODUCTS_PAGE -> {
                DefaultView(defaultViewViewModel, viewModifier)
            }

            NavigationDestination.ORDERS_PAGE -> {
                OrdersView(orderViewViewModel)
            }

            NavigationDestination.SETTINGS_PAGE -> {
                SettingsView(settingsViewModel)
            }
        }

        when (activeAlert.value) {
            AlertType.UNSAVED_CHANGES -> {
                SingleActionAlertDialog(
                    text = getText("warning.unsaved.changes"),
                    actionButtonText = getText("label.acknowledge")
                ) {
                    alertManager.setActiveAlert(null)
                }
            }

            else -> {}
        }
    }
}

private fun initializeProviders(
    activeLanguage: SupportedLanguage,
    windowSize: DpSize
) {
    TextProviderImpl(activeLanguage)
    AppStyleProviderImpl(windowSize)
}