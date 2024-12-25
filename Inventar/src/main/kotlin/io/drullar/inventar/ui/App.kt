package io.drullar.inventar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.views.OrdersView
import io.drullar.inventar.ui.components.views.products.ProductViewModel
import io.drullar.inventar.ui.components.views.products.ProductsView
import io.drullar.inventar.ui.components.search.SearchBar

@Composable
fun App() {
    val productViewModel = ProductViewModel()
    var currentView by remember { mutableStateOf(NavigationDestination.PRODUCTS_PAGE) }
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            NavigationBar(
                selectedView = currentView,
                modifier = Modifier.heightIn(30.dp, 40.dp).widthIn(100.dp, 300.dp),
                onNavigationChange = { currentView = it }
            )

            SearchBar(
                modifier = Modifier.heightIn(30.dp, 40.dp).fillMaxWidth(0.5f),
                onSearchSubmit = { /*TODO search implementation*/ })
        }

        val viewModifier = Modifier.fillMaxWidth().fillMaxHeight()
        val destinationToScreen = mapOf<NavigationDestination, @Composable () -> Unit>(
            NavigationDestination.PRODUCTS_PAGE to {
                ProductsView(
                    productViewModel,
                    viewModifier
                )
            },
            NavigationDestination.ORDERS_PAGE to { OrdersView() }
        )

        destinationToScreen[currentView]!!()
    }


}

