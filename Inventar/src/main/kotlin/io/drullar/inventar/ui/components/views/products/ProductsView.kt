package io.drullar.inventar.ui.components.views.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.cards.ProductDetailedViewCard
import io.drullar.inventar.ui.components.dialog.NewProductDialog
import io.drullar.inventar.ui.components.dialog.AlertDialog
import io.drullar.inventar.ui.components.views.products.layout.DraftOrderButton
import io.drullar.inventar.ui.components.views.products.layout.ProductUtilBar
import io.drullar.inventar.ui.style.roundedBorder
import io.drullar.inventar.ui.utils.Icons

@Composable
fun ProductsView(
    viewModel: ProductViewModel, modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val showUnsavedChangesAlert by viewModel.showUnsavedChangesAlert.collectAsState()
    val showNewProductDialog by viewModel.showNewProductDialog.collectAsState()
    val preview by viewModel.preview.collectAsState()
    val previewChangeIsAllowed = viewModel.previewChangeIsAllowed.collectAsState()
    val draftOrders = viewModel.draftOrders.collectAsState()
    val orderButtonCount = viewModel.orderButtonCount.collectAsState()

    if (showUnsavedChangesAlert) {
        UnsavedChangesAlert(
            onCancel = { viewModel.updateShowUnsavedChangesAlert(false) },
            onResolve = {
                viewModel.updateProduct((preview as DetailedProductPreview).getPreviewData())
                viewModel.updateShowUnsavedChangesAlert(false)
            }
        )
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().heightIn(30.dp, 70.dp)) {
            ProductUtilBar(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterVertically),
                onNewProductButtonClick = {
                    viewModel.updateShowNewProductDialog(true)
                }
            )

            DraftOrderButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                draftOrdersCount = orderButtonCount.value,
                onClick = { viewModel.handleOrdersButtonClick() })
        }


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
                    selectionIsAllowed = previewChangeIsAllowed.value
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight()
                    .roundedBorder()
            ) {
                when (preview) {
                    is DetailedProductPreview -> {
                        val data = (preview as DetailedProductPreview).getPreviewData()
                        ProductDetailedViewCard(
                            productData = data,
                            onChange = {
                                viewModel.forbidPreviewChange()
                            },
                            onRevert = {
                                viewModel.allowPreviewChange()
                                data
                            },
                            onSave = { updatedProductDTO ->
                                viewModel.updateProduct(updatedProductDTO)
                            },
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    is OrderCreationPreview -> {
                        // TODO
                    }
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
private fun UnsavedChangesAlert(onCancel: () -> Unit, onResolve: () -> Unit) {
    AlertDialog(
        text = "There are unsaved changes to a product you're editing. " +
                "Save or revert the changes in order to select to continue",
        resolveButtonText = "Save changes",
        onResolve = onResolve,
        onCancel = onCancel
    )
}