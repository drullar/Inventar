package io.drullar.inventar

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.ui.App
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.viewmodel.delegates.impl.AlertManagerImpl
import io.drullar.inventar.ui.viewmodel.delegates.impl.SharedAppStateDelegateImpl
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.SettingsViewModel
import io.drullar.inventar.ui.viewmodel.delegates.impl.SettingsProviderImpl
import io.drullar.inventar.utils.bootstrap.ApplicationBootstrapper
import io.drullar.inventar.utils.bootstrap.DatabaseBootstrapperImpl
import io.drullar.inventar.utils.file.FileManager
import java.awt.Dimension

fun main() {
    val fileManager = FileManager()
    ApplicationBootstrapper(
        fileManager = fileManager,
        databaseBootstrapperFactory = { databaseFile -> DatabaseBootstrapperImpl(databaseFile) }
    ).bootstrap()

    val sharedAppStateHolder = SharedAppStateDelegateImpl(NavigationDestination.PRODUCTS_PAGE)
    val alertManagerDelegate = AlertManagerImpl()
    val settingsProvider = SettingsProviderImpl(fileManager)

    val defaultViewViewModel =
        DefaultViewViewModel(sharedAppStateHolder, alertManagerDelegate, settingsProvider)
    val orderViewViewModel = OrderViewViewModel(sharedAppStateHolder, settingsProvider)
    val settingsViewModel = SettingsViewModel(settingsProvider)

    application {
        Window(
            ::exitApplication,
            title = "Inventar",
            state = rememberWindowState(
                placement = WindowPlacement.Maximized
            ),
            icon = painterResource(Icons.APP_ICON)
        ) {
            window.minimumSize = Dimension(800, 600)
            App(
                sharedAppStateHolder,
                alertManagerDelegate,
                defaultViewViewModel,
                orderViewViewModel,
                settingsViewModel,
            )
        }
    }
}