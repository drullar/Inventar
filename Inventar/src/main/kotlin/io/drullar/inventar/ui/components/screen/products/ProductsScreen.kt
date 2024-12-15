package io.drullar.inventar.ui.components.screen.products

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.service.ProductsService
import io.drullar.inventar.ui.components.PREVIEW_COMPONENT_DEPRECATION_MESSAGE
import io.drullar.inventar.ui.components.cards.ProductDetailedViewCard
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.dialog.NewProductDialog
import io.drullar.inventar.ui.components.dialog.UnsavedChangesAlertDialog
import io.drullar.inventar.ui.components.screen.products.layout.ProductUtilBar
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductsScreen(navigationBar: @Composable () -> Unit) {
    val productsService = ProductsService()
    val products by remember { mutableStateOf(productsService.getAll().toMutableList()) }
    val showNewProductDialog = remember { mutableStateOf(false) }
    val selectedProduct by remember { mutableStateOf<ProductDTO?>(null) }
    var detailedProductCardHasChange by remember { mutableStateOf(false) }
    var showUnsavedChangesAlert by remember { mutableStateOf(false) }

    if (showUnsavedChangesAlert) {
        unsavedChangesAlert { showUnsavedChangesAlert = false }
    }

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
                ProductsLazyGrid(products, {}, detailedProductCardHasChange)
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight()
                    .roundedBorder()
            ) {
                Box(modifier = Modifier.padding(5.dp)) {
                    selectedProduct?.let { selectedProductDTO ->
                        ProductDetailedViewCard(
                            selectedProductDTO,
                            onChange = {
                                detailedProductCardHasChange = true
                            },
                            onTerminalChange = {
                                detailedProductCardHasChange = false
                            }
                        )
                    }

                }
            }
        }
    }

    if (showNewProductDialog.value) {
        NewProductDialog(
            onClose = { showNewProductDialog.value = false },
            onNewProductSubmit = { form ->
                productsService.save(form)
                products.add(form)
            }
        )
    }
}

@Composable
private fun unsavedChangesAlert(onCancel: () -> Unit) {
    UnsavedChangesAlertDialog(
        text = "There are unsaved changes to a product you're editing. \n" +
                "Save or revert the changes in order to select to continue",
        onCancel = onCancel
    )
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