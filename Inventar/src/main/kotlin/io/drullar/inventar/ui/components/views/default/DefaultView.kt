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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.OrderDetailPreviewCard
import io.drullar.inventar.ui.components.cards.OrdersListPreviewCard
import io.drullar.inventar.ui.components.cards.ProductDetailedViewCard
import io.drullar.inventar.ui.components.dialog.NewProductDialog
import io.drullar.inventar.ui.components.dialog.OrderPreviewDialog
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.components.dialog.OrderProductConfirmation
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.layout.DraftOrderButton
import io.drullar.inventar.ui.components.views.default.layout.ProductUtilBar
import io.drullar.inventar.ui.data.DetailedProductPreview
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.data.OrdersListPreview
import io.drullar.inventar.ui.provider.getLayoutStyle
import io.drullar.inventar.ui.style.LayoutStyle
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun DefaultView(
    viewModel: DefaultViewViewModel,
    modifier: Modifier = Modifier
) {
    val layout by remember { mutableStateOf(getLayoutStyle()) }
    val products by viewModel.products.collectAsState()
    val activeDialogWindow by viewModel.getActiveDialog().collectAsState()
    val activeExternalWindow by viewModel.getActiveWindow().collectAsState()
    val preview by viewModel.preview.collectAsState()
    val previewChangeIsAllowed = viewModel.previewChangeIsAllowed.collectAsState()
    val draftOrdersCount = viewModel.draftOrdersCount.collectAsState()
    val settings by viewModel.getSettings().collectAsState()

//    handleLayoutChange(layout, viewModel)

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().heightIn(30.dp, 70.dp)) {
            ProductUtilBar(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                onNewProductButtonClick = {
                    if (!viewModel.hasActiveDialogWindow()) {
                        viewModel.setActiveDialog(DialogWindowType.NEW_PRODUCT, EmptyPayload())
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
                        viewModel.showAddProductToOrderDialog(it)
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
                                viewModel.removeProductFromOrder(product, order)
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
                            style = getLayoutStyle(),
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

    handleDialogWindowRender(activeDialogWindow, viewModel)
    handleActiveExternalWindowRender(activeExternalWindow, viewModel)
}

@Composable
private fun handleDialogWindowRender(
    activeDialogWindow: DialogWindowType?,
    viewModel: DefaultViewViewModel
) {
    when (activeDialogWindow) {
        DialogWindowType.NEW_PRODUCT -> NewProductDialog(
            onClose = { viewModel.closeDialogWindow() },
            onSubmit = {
                viewModel.addNewProduct(it)
                viewModel.closeDialogWindow()
            }
        )

        DialogWindowType.ADD_PRODUCT_TO_ORDER -> {
            val product = viewModel.getActiveDialogPayload<ProductDTO>().getData()
            OrderProductConfirmation(
                product = product,
                initialQuantity = viewModel.getCurrentOrderTargetProductQuantity(product) ?: 1,
                onConfirm = { quantity ->
                    viewModel.addProductToOrder(product, quantity)
                    viewModel.closeDialogWindow()
                },
                onCancel = {
                    viewModel.closeDialogWindow()
                }
            )
        }

        null -> Unit
    }
}

@Composable
private fun handleActiveExternalWindowRender(
    activeExternalWindowType: ExternalWindowType?,
    viewModel: DefaultViewViewModel
) {
    when (activeExternalWindowType) {
        ExternalWindowType.ORDER_PREVIEW -> {
            OrderPreviewDialog(
                orderDTO = viewModel.getActiveWindowPayload<OrderDTO>().getData(),
                onClose = { viewModel.closeExternalWindow() },
                onTerminate = { /* TODO implement */ },
                onComplete = {
                    viewModel.completeOrder(it)
                    viewModel.closeExternalWindow()
                },
                onProductValueChange = { product, quantity ->
                    viewModel.changeProductQuantityInOrder(
                        product,
                        quantity
                    )
                },
                onProductRemove = { product, order ->
                    viewModel.removeProductFromOrder(
                        product,
                        order
                    )
                }
            )
        }

        null -> Unit
    }

}

private fun handleLayoutChange(updatedLayout: LayoutStyle, viewModel: DefaultViewViewModel) {
    val activeWindow = viewModel.getActiveWindow()
    val preview = viewModel.getPreview().value
    when (updatedLayout) {
        LayoutStyle.COMPACT -> {

        }

        LayoutStyle.NORMAL -> {

        }
    }
}