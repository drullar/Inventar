package io.drullar.inventar.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.screen.OrdersScreen
import io.drullar.inventar.ui.components.screen.products.ProductViewModel
import io.drullar.inventar.ui.components.screen.products.ProductsScreen

@Composable
fun App() {
    val currentScreen = remember { mutableStateOf(NavigationDestination.PRODUCTS_PAGE) }
    val navigationBar: @Composable () -> Unit = @Composable {
        val navBarModifier = Modifier.padding(10.dp)

        NavigationBar(currentScreen.value, navBarModifier) { newDestination ->
            currentScreen.value = newDestination
            println("Changing destination to ${newDestination.name}")
        }
    }

    val productViewModel = ProductViewModel()

    val destinationToScreen = mapOf<NavigationDestination, @Composable () -> Unit>(
        NavigationDestination.PRODUCTS_PAGE to { ProductsScreen(productViewModel) { navigationBar() } },
        NavigationDestination.ORDERS_PAGE to { OrdersScreen { navigationBar() } }
    )

    destinationToScreen[currentScreen.value]!!()
}

