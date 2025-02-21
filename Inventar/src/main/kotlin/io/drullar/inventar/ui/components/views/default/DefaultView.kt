package io.drullar.inventar.ui.components.views.default

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.cards.OrderCreationCard
import io.drullar.inventar.ui.components.cards.OrdersListPreviewCard
import io.drullar.inventar.ui.components.cards.ProductDetailedViewCard
import io.drullar.inventar.ui.components.dialog.NewProductDialog
import io.drullar.inventar.ui.components.dialog.AlertDialog
import io.drullar.inventar.ui.components.dialog.DialogType
import io.drullar.inventar.ui.components.dialog.OrderProductConfirmation
import io.drullar.inventar.ui.components.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.layout.DraftOrderButton
import io.drullar.inventar.ui.components.views.default.layout.ProductUtilBar
import io.drullar.inventar.ui.style.roundedBorder
import kotlinx.coroutines.runBlocking

@Composable
fun DefaultView(
    viewModel: DefaultViewViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val dialog by viewModel.dialogToDisplay.collectAsState()
    val preview by viewModel.preview
    val previewChangeIsAllowed = viewModel.previewChangeIsAllowed.collectAsState()
    val draftOrdersCount = viewModel.draftOrdersCount.collectAsState()

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().heightIn(30.dp, 70.dp)) {
            ProductUtilBar(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.CenterVertically),
                onNewProductButtonClick = {
                    viewModel.showNewProductDialog()
                }
            )

            DraftOrderButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                draftOrdersCount = draftOrdersCount.value,
                onClick = { viewModel.showDraftOrders() })
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
                    products = products ?: emptyList(),
                    onProductSelectCallback = { clickedProductData ->
                        viewModel.selectProduct(clickedProductData)
                    },
                    selectionIsAllowed = previewChangeIsAllowed.value,
                    onProductEditRequest = { productDTO ->
                        viewModel.selectProduct(productDTO)
                    },
                    onProductDeleteRequest = { productDTO ->
                        viewModel.deleteProduct(productDTO)
                    },
                    onAddProductToOrderRequest = {
                        viewModel.showOrderProductDialog(it)
                    }
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
                                viewModel.allowPreviewChange(false)
                            },
                            onRevert = {
                                viewModel.allowPreviewChange(true)
                                data
                            },
                            onSave = { updatedProductDTO ->
                                viewModel.updateProduct(updatedProductDTO)
                            },
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    is OrderCreationPreview -> {
                        val data = (preview as OrderCreationPreview).getPreviewData()
                        OrderCreationCard(
                            order = data,
                            onCancel = {},
                            onComplete = {},
                            onProductRemove = { product ->
                                viewModel.removeProductFromOrder(product)
                            }
                        )
                    }

                    is OrdersListPreview -> {
                        val draftOrders = (preview as OrdersListPreview).getPreviewData()
                        OrdersListPreviewCard(
                            draftOrders = draftOrders,
                            onOrderCompletion = { completedOrder ->
                                // TODO
                            },
                            onOrderSelect = { selectedOrder ->
                                // TODO
                            })
                    }
                }
            }
        }
    }

    // Pop up dialogs/alerts/forms in a new window onFocus
    when (dialog) {
        DialogType.NEW_PRODUCT -> NewProductDialog(
            onClose = { viewModel.closeCurrentDialog() },
            onSubmit = { viewModel.addNewProduct(it) }
        )

        DialogType.UNSAVED_CHANGES_ALERT -> UnsavedChangesAlert(
            onCancel = { viewModel.closeCurrentDialog() },
            onResolve = {
                viewModel.updateProduct((preview as DetailedProductPreview).getPreviewData())
                viewModel.closeCurrentDialog()
            }
        )

        DialogType.ADD_PRODUCT_TO_ORDER -> {
            OrderProductConfirmation(
                viewModel.targetProduct.value!!,
                onConfirm = { quantity ->
                    viewModel.addProductToOrder(quantity)
                    viewModel.closeCurrentDialog()
                },
                onCancel = {
                    viewModel.targetProduct.value = null
                    viewModel.closeCurrentDialog()
                }
            )
        }

        DialogType.NONE -> Unit
        else -> throw NotImplementedError("Dialog rendering for ${dialog.name} is not implemented")
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