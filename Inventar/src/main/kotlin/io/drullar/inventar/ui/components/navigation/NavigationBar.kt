package io.drullar.inventar.ui.components.navigation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.utils.Icons

@Composable
fun NavigationBar(
    selectedScreen: NavigationDestination,
    onItemSelect: (destination: NavigationDestination) -> Unit
) {
    val spacingBetweenElements = 10.dp
    val selectedItemDetails = navigationItems[selectedScreen]

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacingBetweenElements)
    ) {
        navigationItems.forEach { (destination, details) ->
            NavigationItem(
                details = details,
                isSelected = selectedItemDetails == details,
                onClick = {
                    onItemSelect(destination)
                }
            )
        }
    }
}

@Preview
@Composable
@Deprecated(
    "Used only for preview purposes",
    ReplaceWith("NavigationBar(...)")
)
internal fun NavigationBarPreviewContainer() {
    NavigationBar(NavigationDestination.PRODUCTS_PAGE, {})
}

private val navigationItems by lazy {
    sortedMapOf(
        NavigationDestination.PRODUCTS_PAGE to NavigationItemDetails(
            iconPath = Icons.PRODUCTS_ICON,
            "Products"
        ),
        NavigationDestination.ORDERS_PAGE to NavigationItemDetails(
            iconPath = Icons.ORDERS_ICON,
            "Orders"
        )
    )
}
