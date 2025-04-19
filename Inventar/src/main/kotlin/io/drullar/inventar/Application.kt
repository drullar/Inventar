package io.drullar.inventar

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.drullar.inventar.ui.ComposeApp
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.delegate.ViewModelFactory
import io.drullar.inventar.utils.bootstrap.ApplicationBootstrapper
import io.drullar.inventar.utils.bootstrap.DatabaseBootstrapperImpl
import io.drullar.inventar.utils.scanner.ScannerInputHandler.handleEvent
import java.awt.Button
import java.awt.Dialog
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Label

class Application {
    private val viewModelFactory = ViewModelFactory()

    init {
        ApplicationBootstrapper(
            fileManager = viewModelFactory.fileManager,
            databaseBootstrapperFactory = { databaseFile -> DatabaseBootstrapperImpl(databaseFile) }
        ).bootstrap()
    }

    fun run() {
        Thread.setDefaultUncaughtExceptionHandler { _, e -> //TODO proper error handling
//            Dialog(Frame(), e.message ?: "Error").apply {
//                layout = FlowLayout()
//                val label = Label(e.stackTraceToString())
//                add(label)
//                val button = Button("OK").apply {
//                    addActionListener {
//                        dispose()
//                        Thread.currentThread().interrupt()
//                    }
//                }
//                add(button)
//                setSize(300, 300)
//                isVisible = true
//            }

            e.printStackTrace()
        }

        application {
            val windowState = rememberWindowState(placement = WindowPlacement.Maximized)
            Window(
                onCloseRequest = { exitApplication() },
                title = "Inventar",
                state = windowState,
                icon = painterResource(Icons.APP_ICON),
                onKeyEvent = { event ->
                    handleEvent(event, viewModelFactory.barcodeScanManager)
                }
            ) {
                window.minimumSize = Dimension(MIN_WIDTH, MIN_HEIGHT)
                MenuBar {
                    Menu("File") {
                        Item("Export") {
                            viewModelFactory.globalWindowManager.setActiveWindow(
                                ExternalWindowType.DATA_EXPORT,
                                EmptyPayload()
                            )
                        }
                    }
                }

                ComposeApp(
                    viewModelFactory,
                    windowState.size
                )
            }
        }
    }

    companion object {
        private const val MIN_WIDTH = 1020
        private const val MIN_HEIGHT = 600
    }
}