package io.drullar.inventar.ui.components.views.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.cards.ProductDetailedViewCard
import io.drullar.inventar.ui.components.dialog.NewProductDialog
import io.drullar.inventar.ui.components.dialog.UnsavedChangesAlertDialog
import io.drullar.inventar.ui.components.views.products.layout.ProductUtilBar
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductsView(
    viewModel: ProductViewModel, modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val detailedProductCardHasChange by viewModel.selectedProductHasChanges.collectAsState()
    val selectedProductDTO by viewModel.selectedProduct.collectAsState()
    val showUnsavedChangesAlert by viewModel.showUnsavedChangesAlert.collectAsState()
    val showNewProductDialog by viewModel.showNewProductDialog.collectAsState()

    if (showUnsavedChangesAlert) {
        UnsavedChangesAlert(
            onCancel = { viewModel.updateShowUnsavedChangesAlert(false) }
        )
    }

    Column(modifier = modifier) {
        ProductUtilBar(
            modifier = Modifier
                .heightIn(30.dp, 70.dp),
            onNewProductButtonClick = {
                viewModel.updateShowNewProductDialog(true)
            }
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
                ProductsLazyGrid(
                    products = products,
                    onProductSelectCallback = { clickedProductData ->
                        viewModel.selectProduct(clickedProductData)
                    },
                    selectionIsAllowed = !detailedProductCardHasChange
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight()
                    .roundedBorder()
            ) {
                //TODO add some sort of abstraction to switch between selectedProduct and OrderCreationCard

                if (selectedProductDTO != null) {
                    ProductDetailedViewCard(
                        productData = selectedProductDTO!!,
                        onChange = {
                            viewModel.updateSelectedProductHasChanges(true)
                        },
                        onRevert = {
                            viewModel.updateSelectedProductHasChanges(false)
                            selectedProductDTO!!
                        },
                        onSave = { updatedProductDTO ->
                            viewModel.updateProduct(updatedProductDTO)
                        },
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }

    if (showNewProductDialog) {
        NewProductDialog(
            onClose = { viewModel.updateShowNewProductDialog(false) },
            onSubmit = { viewModel.addNewProduct(it) }
        )
    }
}

@Composable
private fun UnsavedChangesAlert(onCancel: () -> Unit) {
    UnsavedChangesAlertDialog(
        text = "There are unsaved changes to a product you're editing. \n" +
                "Save or revert the changes in order to select to continue",
        onCancel = onCancel
    )
}