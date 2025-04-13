package io.drullar.inventar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.drullar.inventar.ui.components.window.dialog.SingleActionAlertDialog
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.views.analytics.AnalyticsView
import io.drullar.inventar.ui.components.views.order.OrdersView
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.DefaultView
import io.drullar.inventar.ui.components.views.settings.SettingsView
import io.drullar.inventar.ui.components.window.dialog.ExportResultDialog
import io.drullar.inventar.ui.components.window.external.DataExportWindow
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.viewmodel.delegate.AlertManager
import io.drullar.inventar.ui.viewmodel.delegate.SharedAppStateDelegate
import io.drullar.inventar.ui.data.AlertType
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.data.ExportCompletionPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.data.WindowPayload
import io.drullar.inventar.ui.provider.getLayoutStyle
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.SettingsViewModel
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.provider.impl.LayoutStyleProviderImpl
import io.drullar.inventar.ui.provider.impl.TextProviderImpl
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import io.drullar.inventar.ui.viewmodel.delegate.WindowManagerFacade
import io.drullar.inventar.ui.viewmodel.delegate.impl.OrderDataCsvExporter
import io.drullar.inventar.utils.file.DataExportFile
import io.drullar.inventar.utils.file.ExportRequest
import io.drullar.inventar.utils.runAsync
import java.util.Locale

@Composable
fun ComposeApp(
    sharedAppState: SharedAppStateDelegate,
    alertManager: AlertManager,
    defaultViewViewModel: DefaultViewViewModel,
    orderViewViewModel: OrderViewViewModel,
    settingsViewModel: SettingsViewModel,
    analyticsViewModel: AnalyticsViewModel,
    windowManager: WindowManagerFacade,
    windowSize: DpSize
) {
    val activeExternalWindow = windowManager.getActiveWindow().collectAsState()
    val activeDialogWindow = windowManager.getActiveDialog().collectAsState()

    val settingsState = settingsViewModel.getSettings().collectAsState()
    val activeLanguage = settingsState.value.language

    initializeProviders(activeLanguage, windowSize)

    val currentView = sharedAppState.getNavigationDestination().collectAsState()
    val activeAlert = alertManager.getActiveAlert().collectAsState()

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            NavigationBar(
                selectedView = currentView.value,
                onNavigationChange = { sharedAppState.setNavigationDestination(it) }
            )

            Row {
                IconButton(
                    onClick = { sharedAppState.setNavigationDestination(NavigationDestination.SETTINGS_PAGE) },
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

                IconButton(
                    onClick = { sharedAppState.setNavigationDestination(NavigationDestination.ANALYTICS_PAGE) },
                    buttonColors = ButtonColors(
                        Color.Transparent,
                        Color.Black,
                        Color.Transparent,
                        Color.Black
                    )
                ) {
                    Icon(
                        painterResource(Icons.ANALYTICS),
                        getText("label.analytics"),
                        Modifier.height(20.dp)
                    )
                }
            }
        }

        val viewModifier = Modifier.fillMaxWidth().fillMaxHeight()
        when (currentView.value) {
            NavigationDestination.PRODUCTS_PAGE -> {
                DefaultView(defaultViewViewModel, viewModifier, getLayoutStyle())
            }

            NavigationDestination.ORDERS_PAGE -> {
                OrdersView(orderViewViewModel)
            }

            NavigationDestination.SETTINGS_PAGE -> {
                SettingsView(settingsViewModel)
            }

            NavigationDestination.ANALYTICS_PAGE -> {
                AnalyticsView(analyticsViewModel)
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

    handleExternalWindowRender(
        externalWindowType = activeExternalWindow.value,
        onWindowClose = { windowManager.setActiveWindow(null, EmptyPayload()) },
        locale = activeLanguage.locale,
        onDataExport = { request ->
            runAsync {
                val file =
                    OrderDataCsvExporter(settingsViewModel.getSettings().value.defaultCurrency).export(
                        request
                    )

                windowManager.setActiveWindow(null, EmptyPayload())
                windowManager.setActiveDialog(
                    DialogWindowType.EXPORT_RESULT,
                    ExportCompletionPayload(file)
                )
            }
        }
    )

    handleDialogWindowRender(
        dialogWindowType = activeDialogWindow.value,
        dialogWindowPayload = windowManager.getActiveDialogPayload<Any>().value,
        onWindowClose = {
            windowManager.setActiveDialog(null, EmptyPayload())
        }
    )
}

@Composable
private fun handleExternalWindowRender(
    externalWindowType: ExternalWindowType?,
    onWindowClose: () -> Unit,
    locale: Locale,
    onDataExport: (ExportRequest) -> Unit
) {
    when (externalWindowType) {
        ExternalWindowType.DATA_EXPORT -> {
            DataExportWindow(
                onClose = onWindowClose,
                locale = locale,
                onExportRequest = { exportRequestData ->
                    onDataExport(exportRequestData)
                })
        }

        else -> Unit
    }
}

@Composable
private fun handleDialogWindowRender(
    dialogWindowType: DialogWindowType?,
    dialogWindowPayload: WindowPayload<Any>?,
    onWindowClose: () -> Unit
) {
    when (dialogWindowType) {
        DialogWindowType.EXPORT_RESULT -> {
            ExportResultDialog(
                dialogWindowPayload!!.getData() as DataExportFile, onAcknowledge = onWindowClose
            )
        }

        else -> Unit
    }
}

private fun initializeProviders(
    activeLanguage: SupportedLanguage,
    windowSize: DpSize
) {
    TextProviderImpl(activeLanguage)
    LayoutStyleProviderImpl(windowSize)
}