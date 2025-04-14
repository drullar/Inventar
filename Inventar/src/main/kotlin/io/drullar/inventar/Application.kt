package io.drullar.inventar

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.drullar.inventar.ui.ComposeApp
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.viewmodel.SettingsViewModel
import io.drullar.inventar.ui.viewmodel.delegate.impl.AlertManagerImpl
import io.drullar.inventar.ui.viewmodel.delegate.impl.BarcodeScanManager
import io.drullar.inventar.ui.viewmodel.delegate.impl.SettingsProviderImpl
import io.drullar.inventar.ui.viewmodel.delegate.impl.SharedAppStateDelegateImpl
import io.drullar.inventar.ui.viewmodel.delegate.impl.WindowManagerFacadeImpl
import io.drullar.inventar.utils.bootstrap.ApplicationBootstrapper
import io.drullar.inventar.utils.bootstrap.DatabaseBootstrapperImpl
import io.drullar.inventar.utils.file.FileManager
import java.awt.Button
import java.awt.Dialog
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Label

class Application {

    fun run() {
        Thread.setDefaultUncaughtExceptionHandler { _, e -> //TODO cleanup error handling
            Dialog(Frame(), e.message ?: "Error").apply {
                layout = FlowLayout()
                val label = Label(e.stackTraceToString())
                add(label)
                val button = Button("OK").apply {
                    addActionListener {
                        dispose()
                        Thread.currentThread().interrupt()
                    }
                }
                add(button)
                setSize(300, 300)
                isVisible = true
            }

            e.printStackTrace()
        }

        val fileManager = FileManager()
        ApplicationBootstrapper(
            fileManager = fileManager,
            databaseBootstrapperFactory = { databaseFile -> DatabaseBootstrapperImpl(databaseFile) }
        ).bootstrap()

        val sharedAppStateHolder = SharedAppStateDelegateImpl(NavigationDestination.PRODUCTS_PAGE)
        val alertManagerDelegate = AlertManagerImpl()
        val settingsProvider = SettingsProviderImpl(fileManager)

        val barcodeScanManager = BarcodeScanManager()
        val defaultViewViewModel =
            DefaultViewViewModel(
                sharedAppStateHolder,
                alertManagerDelegate,
                settingsProvider,
                barcodeScanManager
            )
        val orderViewViewModel = OrderViewViewModel(sharedAppStateHolder, settingsProvider)
        val settingsViewModel = SettingsViewModel(settingsProvider)
        val analyticsViewModel = AnalyticsViewModel(settingsProvider)
        val globalWindowManager = WindowManagerFacadeImpl()

        application {
            val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
            val listeningForBarcode by barcodeScanManager.isListening.collectAsState()
            Window(
                onCloseRequest = { exitApplication() },
                title = "Inventar",
                state = windowState,
                icon = painterResource(Icons.APP_ICON),
                onKeyEvent = { event ->
                    if (listeningForBarcode && event.type == KeyEventType.KeyDown) {
                        if (event.key == Key.Enter) {
                            barcodeScanManager.complete()
                        } else {
                            val character = event.utf16CodePoint.toChar()
                            barcodeScanManager.notify(character)
                        }
                        return@Window true
                    }
                    false
                }
            ) {
                window.minimumSize = Dimension(MIN_WIDTH, MIN_HEIGHT)

                MenuBar {
                    Menu("File") {
                        Item("Export") {
                            globalWindowManager.setActiveWindow(
                                ExternalWindowType.DATA_EXPORT,
                                EmptyPayload()
                            )
                        }
                    }
                }

                ComposeApp(
                    sharedAppStateHolder,
                    alertManagerDelegate,
                    defaultViewViewModel,
                    orderViewViewModel,
                    settingsViewModel,
                    analyticsViewModel,
                    globalWindowManager,
                    windowState.size
                )
            }
        }
    }

    companion object {
        const val MIN_WIDTH = 1020
        const val MIN_HEIGHT = 600
    }
}