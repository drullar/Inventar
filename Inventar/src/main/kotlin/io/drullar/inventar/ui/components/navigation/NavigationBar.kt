package io.drullar.inventar.ui.components.navigation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    val selectedItemDetails = navigationItems[selectedView]

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacingBetweenElements)
    ) {
        navigationItems.forEach { (destination, details) ->
            NavigationItem(
                details = details,
                isSelected = selectedItemDetails == details,
                onClick = {
                    onNavigationChange(destination)
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
    NavigationBar(NavigationDestination.PRODUCTS_PAGE) {}
}

private val navigationItems by lazy {
    sortedMapOf(
        NavigationDestination.PRODUCTS_PAGE to NavigationItemDetails(
            iconPath = Icons.PRODUCTS,
            getText("label.main")
        ),
        NavigationDestination.ORDERS_PAGE to NavigationItemDetails(
            iconPath = Icons.ORDERS_LOG,
            getText("label.log.history")
        )
    )
}
