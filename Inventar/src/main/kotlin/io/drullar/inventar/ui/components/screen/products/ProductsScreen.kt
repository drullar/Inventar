package io.drullar.inventar.ui.components.screen.products

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.PREVIEW_COMPONENT_DEPRECATION_MESSAGE
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.screen.products.child.components.NewDialogProduct
import io.drullar.inventar.ui.components.screen.products.child.components.ProductUtilBar
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductsScreen(navigationBar: @Composable () -> Unit) {
    val showNewProductDialog = remember { mutableStateOf(false) }

    Column {
        navigationBar()
        ProductUtilBar(
            onNewProductButtonClick = { showNewProductDialog.value = true }
        )

        // Main content
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(10.dp)
                    .roundedBorder()
                    .fillMaxHeight(1f)
            ) {
                Box(modifier = Modifier.padding(5.dp)) {
                    Text("Content area")
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight()
                    .roundedBorder()
            ) {
                Box(modifier = Modifier.padding(5.dp)) {
                    Text("Detailed info area")
                }
            }
        }
    }

    if (showNewProductDialog.value) {
        NewDialogProduct(
            onClose = { showNewProductDialog.value = false },
            onNewProductSubmit = { form ->
                form //TODO persist form data
            }
        )
    }
}

@Preview
@Composable
@Deprecated(
    PREVIEW_COMPONENT_DEPRECATION_MESSAGE, ReplaceWith(
        "ProductsScreen { NavigationBar() }"
    )
)

internal fun ProductsScreenPreviewContainer() {
    ProductsScreen {
        NavigationBar(
            selectedScreen = NavigationDestination.PRODUCTS_PAGE,
            Modifier.padding(10.dp),
        ) {}
    }
}