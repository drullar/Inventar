package io.drullar.inventar.ui.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.provider.getText

@Composable
fun NavigationBar(
    selectedView: NavigationDestination,
    modifier: Modifier = Modifier,
    onNavigationChange: (destination: NavigationDestination) -> Unit
) {
    val spacingBetweenElements = 10.dp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacingBetweenElements)
    ) {
        NavigationItem(
            details = NavigationItemDetails(
                iconPath = Icons.PRODUCTS,
                getText("label.main")
            ),
            isSelected = selectedView == NavigationDestination.PRODUCTS_PAGE,
            onClick = {
                onNavigationChange(NavigationDestination.PRODUCTS_PAGE)
            }
        )

        NavigationItem(
            details = NavigationItemDetails(
                iconPath = Icons.ORDERS_LOG,
                getText("label.log.history")
            ),
            isSelected = selectedView == NavigationDestination.ORDERS_PAGE,
            onClick = {
                onNavigationChange(NavigationDestination.ORDERS_PAGE)
            }
        )
    }
}
