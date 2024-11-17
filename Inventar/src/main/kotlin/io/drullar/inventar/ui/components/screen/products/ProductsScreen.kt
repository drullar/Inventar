package io.drullar.inventar.ui.components.screen.products

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.payload.ProductDetailedPayload
import io.drullar.inventar.ui.components.PREVIEW_COMPONENT_DEPRECATION_MESSAGE
import io.drullar.inventar.ui.components.cards.ProductDetailedPreviewCard
import io.drullar.inventar.ui.components.cards.ProductPreviewCard
import io.drullar.inventar.ui.components.navigation.NavigationBar
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.dialog.NewDialogProduct
import io.drullar.inventar.ui.components.dialog.UnsavedChangesAlertDialog
import io.drullar.inventar.ui.components.screen.products.layout.ProductUtilBar
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductsScreen(navigationBar: @Composable () -> Unit) {
    val showNewProductDialog = remember { mutableStateOf(false) }
    val products = mutableListOf<@Composable (index: Int) -> Unit>()
    var selectedProductDetails by remember { mutableStateOf<ProductDetailedPayload?>(null) }
    var detailedProductCardHasChange by remember { mutableStateOf(false) }
    var showUnsavedChangesAlert by remember { mutableStateOf(false) }

    if (showUnsavedChangesAlert) {
        unsavedChangesAlert { showUnsavedChangesAlert = false }
    }

    for (i in 1..100) {
        products.add(0) { index ->
            val productDetails = ProductDetailedPayload(
                "$index This is a really really really long name This is a really really really long name",
                0.0
            )
            ProductPreviewCard(
                productDetails,
                onClickCallback = { data ->
                    if (data != null) {
                        selectedProductDetails = data
                    } else showUnsavedChangesAlert = true
                }, //TODO render detailed card
                isSelected = selectedProductDetails == productDetails,
                selectionIsAllowed = !detailedProductCardHasChange
            )
        }
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
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    itemsIndexed(products) { index, productCard ->
                        Box(modifier = Modifier.padding(5.dp)) {
                            productCard(index)
                        }

                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight()
                    .roundedBorder()
            ) {
                Box(modifier = Modifier.padding(5.dp)) {
                    ProductDetailedPreviewCard(
                        ProductDetailedPayload(
                            "This is a really really really long name This is a really really really long name",
                            0.0
                        ),
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

    if (showNewProductDialog.value) {
        NewDialogProduct(
            onClose = { showNewProductDialog.value = false },
            onNewProductSubmit = { form ->
                form //TODO persist form data
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