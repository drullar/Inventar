package io.drullar.inventar.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.navigation.NavigationItem
import io.drullar.inventar.ui.components.screen.OrdersScreen
import io.drullar.inventar.ui.components.screen.ProductsScreen
import io.drullar.inventar.ui.routing.Screen
import io.drullar.inventar.ui.routing.ScreenManager
import io.drullar.inventar.ui.routing.routingTableOf

@Composable
fun App() {
    val currentScreen = remember { mutableStateOf(NavigationDestination.PRODUCTS_PAGE) }
    val navigationBar = @Composable {
        NavigationBar(currentScreen.value) { newDestination ->
            currentScreen.value = newDestination
            println("Changing destination to ${newDestination.name}")
        }
    }

    val destinationToScreen = mapOf<NavigationDestination, @Composable () -> Unit>(
        NavigationDestination.PRODUCTS_PAGE to { ProductsScreen { navigationBar() } },
        NavigationDestination.ORDERS_PAGE to { OrdersScreen { navigationBar() } }
    )

    destinationToScreen[currentScreen.value]!!()
}

