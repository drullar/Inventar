package io.drullar.inventar

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.drullar.inventar.ui.App
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.viewmodel.delegates.impl.AlertManagerImpl
import io.drullar.inventar.ui.viewmodel.delegates.impl.SharedAppStateDelegateImpl
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.utils.Bootstrapper
import java.awt.Dimension

fun main() {
    Bootstrapper().bootstrapApplication()
    val sharedAppStateHolder = SharedAppStateDelegateImpl(NavigationDestination.PRODUCTS_PAGE)
    val alertManagerDelegate = AlertManagerImpl()
    val defaultViewViewModel = DefaultViewViewModel(sharedAppStateHolder, alertManagerDelegate)
    val orderViewViewModel = OrderViewViewModel(sharedAppStateHolder)

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
                orderViewViewModel
            )
        }
    }
}