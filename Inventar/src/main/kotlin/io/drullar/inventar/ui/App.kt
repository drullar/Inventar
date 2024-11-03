package io.drullar.inventar.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationItem
import io.drullar.inventar.ui.components.screen.OrdersScreen
import io.drullar.inventar.ui.components.screen.ProductsScreen
import io.drullar.inventar.ui.routing.Screen
import io.drullar.inventar.ui.routing.ScreenManager
import io.drullar.inventar.ui.routing.routingTableOf

@Composable
fun App() {
    val screen = remember { mutableStateOf(Screen.PRODUCTS) }
    val navigationBar = @Composable {
        NavigationBar {
            Row {
                NavigationItem(textIdentifier = "Products", onClick = {
                    screen.value = Screen.ORDERS
                })
            }
        }
    }

    // TODO better routing, independent of the Composable
    val screenManager = ScreenManager(
        screen.value,
        routingTableOf(
            Screen.PRODUCTS to {
                ProductsScreen(navigationBar = navigationBar) {
                }
            },
            Screen.ORDERS to {
                OrdersScreen(content = navigationBar)
            })
    )
}


