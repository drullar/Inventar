package io.drullar.inventar.ui.components.views.default

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.cards.OrderDetailPreviewCard
import io.drullar.inventar.ui.components.cards.OrdersListPreviewCard
import io.drullar.inventar.ui.components.cards.ProductDetailedViewCard
import io.drullar.inventar.ui.components.dialog.NewProductDialog
import io.drullar.inventar.ui.data.DialogType
import io.drullar.inventar.ui.components.dialog.OrderProductConfirmation
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.layout.DraftOrderButton
import io.drullar.inventar.ui.components.views.default.layout.ProductUtilBar
import io.drullar.inventar.ui.data.DetailedProductPreview
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.data.OrdersListPreview
import io.drullar.inventar.ui.provider.getAppStyle
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun DefaultView(
    viewModel: DefaultViewViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val dialog by viewModel.getActiveDialog().collectAsState()
    val preview by viewModel.preview.collectAsState()
    val previewChangeIsAllowed = viewModel.previewChangeIsAllowed.collectAsState()
    val draftOrdersCount = viewModel.draftOrdersCount.collectAsState()
    val settings by viewModel.getSettings().collectAsState()

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().heightIn(30.dp, 70.dp)) {
            ProductUtilBar(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                onNewProductButtonClick = {
                    if (viewModel.getActiveDialog().value == null) {
                        viewModel.setActiveDialog(DialogType.NEW_PRODUCT)
                    }
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
                    currency = settings.defaultCurrency,
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

                    is OrderDetailsPreview -> {
                        val order = (preview as OrderDetailsPreview).getPreviewData()
                        OrderDetailPreviewCard(
                            order = order,
                            onTerminate = {
                                //TODO
                            },
                            onComplete = {
                                viewModel.completeOrder(order)
                            },
                            onProductRemove = { product ->
                                viewModel.removeProductFromOrder(product)
                            },
                            onProductValueChange = { product, newQuantity ->
                                viewModel.changeProductQuantityInOrder(product, newQuantity)
                            }
                        )
                    }

                    is OrdersListPreview -> {
                        val draftOrders = (preview as OrdersListPreview).getPreviewData()
                        OrdersListPreviewCard(
                            orders = draftOrders,
                            style = getAppStyle(),
                            activeLocale = settings.language.locale,
                            onOrderCompletion = { completedOrder ->
                                viewModel.completeOrder(completedOrder)
                            },
                            onOrderSelect = { selectedOrder ->
                                viewModel.selectOrder(selectedOrder)
                            },
                            onOrderTermination = { terminatedOrder ->
                                //TODO
                            })
                    }
                }
            }
        }
    }

    when (dialog) {
        DialogType.NEW_PRODUCT -> NewProductDialog(
            onClose = { viewModel.setActiveDialog(null) },
            onSubmit = {
                viewModel.addNewProduct(it)
                viewModel.setActiveDialog(null)
            }
        )

        DialogType.ADD_PRODUCT_TO_ORDER -> {
            OrderProductConfirmation(
                viewModel.targetProduct.value!!,
                viewModel.getCurrentOrderTargetProductQuantity() ?: 1,
                onConfirm = { quantity ->
                    viewModel.addProductToOrder(quantity)
                    viewModel.setActiveDialog(null)
                },
                onCancel = {
                    viewModel.targetProduct.value = null
                    viewModel.setActiveDialog(null)
                }
            )
        }

        else -> Unit
    }
}