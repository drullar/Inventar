package io.drullar.inventar.ui.components.views.default

import androidx.annotation.Nullable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.persistence.repositories.impl.ProductsRepository
import io.drullar.inventar.shared.OnScan
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.ui.components.cards.OrderDetailCardRenderContext
import io.drullar.inventar.ui.components.cards.OrderDetailPreviewCard
import io.drullar.inventar.ui.components.cards.OrdersListPreviewCard
import io.drullar.inventar.ui.components.cards.ProductDetailedViewCard
import io.drullar.inventar.ui.components.cards.ProductSummarizedPreviewCard
import io.drullar.inventar.ui.components.window.dialog.NewProductDialog
import io.drullar.inventar.ui.components.window.external.OrderPreviewWindow
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.components.window.dialog.ChangeProductQuantityDialog
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.layout.DraftOrderButton
import io.drullar.inventar.ui.components.views.default.layout.ProductUtilBar
import io.drullar.inventar.ui.components.window.dialog.AlertDialog
import io.drullar.inventar.ui.data.BarcodePayload
import io.drullar.inventar.ui.data.DetailedProductPreview
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.data.OrderWindowPayload
import io.drullar.inventar.ui.data.OrdersListPreview
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.LayoutStyle
import io.drullar.inventar.ui.style.roundedBorder
import java.util.Currency

const val PRODUCTS_PER_PAGE = 40

@Composable
fun DefaultView(
    viewModel: DefaultViewViewModel,
    modifier: Modifier = Modifier,
    layout: LayoutStyle
) {
    val activeDialogWindow by viewModel.getActiveDialog().collectAsState()
    val activeExternalWindow by viewModel.getActiveWindow().collectAsState()
    val previewChangeIsAllowed by viewModel.previewChangeIsAllowed.collectAsState()
    val draftOrdersCount = viewModel.draftOrdersCount.collectAsState()
    val preview by viewModel.preview.collectAsState()
    val settings by viewModel.getSettings().collectAsState()
    val selectedProductId =
        remember { (preview as? DetailedProductPreview)?.getData()?.data?.uid }

    var page by remember { mutableStateOf(1) }
    val sortBy by viewModel._sortBy.collectAsState()
    val sortingOrder by viewModel._sortingOrder.collectAsState()
    val products = remember {
        mutableStateListOf<ProductDTO>().apply {
            addAll(
                viewModel.fetchProducts(
                    PagedRequest(page, PRODUCTS_PER_PAGE, sortingOrder, sortBy)
                ).items
            )
        }
    }

    val lastScannedBarcode by viewModel.lastScannedBarcode.collectAsState()

    LaunchedEffect(lastScannedBarcode) {
        if (lastScannedBarcode.isBlank()) return@LaunchedEffect
        val product = viewModel.searchProducts(
            lastScannedBarcode,
            PagedRequest(0, 1, SortingOrder.DESCENDING, ProductsRepository.SortBy.NAME)
        ).items.firstOrNull()

        if (product == null) {
            viewModel.setActiveDialog(
                DialogWindowType.NEW_PRODUCT,
                BarcodePayload(lastScannedBarcode)
            )
            return@LaunchedEffect
        }

        when (settings.onScan) {
            OnScan.ADD_TO_ORDER -> {
                viewModel.showAddProductToOrderDialog(product)
                viewModel.cleanLastScannedBarcode()
            }

            OnScan.RESTOCK -> {
                viewModel.showChangeProductQuantity(product)
                viewModel.cleanLastScannedBarcode()
            }
        }
    }

    handleLayoutChange(layout, viewModel)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(30.dp, 70.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProductUtilBar(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                onNewProductButtonClick = {
                    if (!viewModel.hasActiveDialogWindow()) {
                        viewModel.setActiveDialog(DialogWindowType.NEW_PRODUCT, EmptyPayload())
                    }
                },
                onSearch = { query ->
                    page = 1
                    products.clear()
                    products.addAll(
                        if (query.isBlank()) {
                            viewModel.fetchProducts(
                                PagedRequest(page, PRODUCTS_PER_PAGE, sortingOrder, sortBy)
                            ).items
                        } else viewModel.searchProducts(
                            query,
                            PagedRequest(page, PRODUCTS_PER_PAGE, sortingOrder, sortBy)
                        ).items //TODO handle pagination of search queries, i.e when scrolling to the end of the displayed searched products to load more
                    )
                }
            )

            DraftOrderButton(
                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 10.dp),
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
                val scrollState = rememberLazyGridState()

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    contentPadding = PaddingValues(10.dp),
                    state = scrollState
                ) {
                    itemsIndexed(
                        items = products,
                        key = { _, product -> product.hashCode() }
                    ) { _, product ->

                        if (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == products.size - 1) {
                            page += 1
                            products.addAll(
                                viewModel.fetchProducts(
                                    PagedRequest(
                                        page,
                                        PRODUCTS_PER_PAGE,
                                        sortingOrder,
                                        sortBy
                                    )
                                ).items
                            )
                        }

                        ProductSummarizedPreviewCard(
                            product,
                            currency = settings.defaultCurrency,
                            onClickCallback = { viewModel.selectProduct(it) },
                            isSelected = product.uid == selectedProductId,
                            selectionIsAllowed = previewChangeIsAllowed,
                            onEditRequest = { viewModel.selectProduct(it) },
                            onDeleteRequest = {
                                viewModel.deleteProduct(it)
                                products.remove(product)
                            },
                            onAddToOrderRequest = { viewModel.showAddProductToOrderDialog(it) }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight()
                    .roundedBorder()
            ) {
                when (preview) {
                    is DetailedProductPreview -> {
                        val product = (preview as DetailedProductPreview).getData().data
                        ProductDetailedViewCard(
                            productData = product,
                            onChange = {
                                viewModel.allowPreviewChange(false)
                            },
                            onRevert = {
                                viewModel.allowPreviewChange(true)
                                product
                            },
                            onSave = { updatedProductDTO ->
                                val updatedValue = viewModel.updateProduct(updatedProductDTO)
                                val originalProductIndex = products.indexOf(product)
                                products[originalProductIndex] = updatedValue
                                viewModel.setPreview<Unit>(null)
                            },
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    is OrderDetailsPreview -> {
                        val order = (preview as OrderDetailsPreview).getData().data
                        OrderDetailPreviewCard(
                            order = order,
                            onTerminate = {
                                viewModel.terminateOrder(order)
                            },
                            onComplete = { hasQuantityIssues ->
                                if (hasQuantityIssues) { // TODO move to some function and reuse in the window.
                                    viewModel.setActiveDialog(
                                        DialogWindowType.ORDER_QUANTITY_ISSUES_ALERT,
                                        OrderWindowPayload(order)
                                    )
                                } else {
                                    val updatedOrder = viewModel.completeOrder(order)
                                    mergeProductChanges(
                                        products,
                                        updatedOrder.productToQuantity.keys
                                    )
                                }
                            },
                            onProductRemove = { product ->
                                viewModel.removeProductFromOrder(product, order)
                            },
                            onProductValueChange = { product, newQuantity ->
                                viewModel.changeProductQuantityInOrder(product, newQuantity)
                            },
                            renderContext = OrderDetailCardRenderContext.PREVIEW,
                            currency = settings.defaultCurrency
                        )
                    }

                    is OrdersListPreview -> {
                        val draftOrders = (preview as OrdersListPreview).getData().data
                        OrdersListPreviewCard(
                            orders = draftOrders,
                            style = layout,
                            activeLocale = settings.language.locale,
                            onOrderCompletion = { completedOrder ->
                                viewModel.completeOrder(completedOrder)
                            },
                            onOrderSelect = { selectedOrder ->
                                viewModel.selectOrder(selectedOrder)
                            },
                            onOrderTermination = { terminatedOrder ->
                            },
                            currency = settings.defaultCurrency
                        )
                    }
                }
            }
        }
    }

    handleDialogWindowRender(
        activeDialogWindow,
        viewModel,
        onAddNewProduct = { productCreationDTO ->
            products.add(viewModel.saveProduct(productCreationDTO))
            viewModel.closeDialogWindow()
            //TODO consider triggering reorder of [products]
        }
    )
    handleActiveExternalWindowRender(
        activeExternalWindowType = activeExternalWindow,
        viewModel = viewModel,
        onOrderCompletionCallback = { completedOrder ->
            mergeProductChanges(products, completedOrder.productToQuantity.keys)
        },
        currency = settings.defaultCurrency
    )
}

@Composable
private fun handleDialogWindowRender(
    activeDialogWindow: DialogWindowType?,
    viewModel: DefaultViewViewModel,
    onAddNewProduct: (ProductCreationDTO) -> Unit
) {
    when (activeDialogWindow) {
        DialogWindowType.NEW_PRODUCT -> {
            val barcodePayload = try {
                viewModel.getActiveDialogPayload<String>().value as BarcodePayload
            } catch (e: TypeCastException) {
                null
            }
            NewProductDialog(
                onClose = { viewModel.closeDialogWindow() },
                onSubmit = onAddNewProduct,
                barcodePayload = barcodePayload
            )
        }

        DialogWindowType.ADD_PRODUCT_TO_ORDER -> {
            val product = viewModel.getActiveDialogPayload<ProductDTO>().value.getData()
            ChangeProductQuantityDialog(
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

        DialogWindowType.ORDER_QUANTITY_ISSUES_ALERT -> {
            val order = viewModel.getActiveDialogPayload<OrderDTO>().value.getData()
            AlertDialog(
                text = getText("warning.order.quantity"),
                resolveButtonText = getText("label.continue.anyway"),
                cancelButtonText = getText("label.cancel"),
                onResolve = {
                    viewModel.completeOrder(order)
                    viewModel.closeDialogWindow()
                },
                onCancel = { viewModel.closeDialogWindow() }
            )
        }

        DialogWindowType.CHANGE_PRODUCT_QUANTITY -> {
            val product = viewModel.getActiveDialogPayload<ProductDTO>().value.getData()
            ChangeProductQuantityDialog(
                product = product,
                initialQuantity = product.availableQuantity,
                onConfirm = { newQuantity ->
                    viewModel.updateProduct(product.copy(availableQuantity = newQuantity))
                    viewModel.setActiveDialog(null, EmptyPayload())
                },
                onCancel = {
                    viewModel.setActiveDialog(null, EmptyPayload())
                }
            )
        }

        else -> Unit
    }
}

@Composable
private fun handleActiveExternalWindowRender(
    activeExternalWindowType: ExternalWindowType?,
    viewModel: DefaultViewViewModel,
    onOrderCompletionCallback: (OrderDTO) -> Unit,
    currency: Currency
) {
    val payload by viewModel.getActiveWindowPayload<Nullable>().collectAsState()

    when (activeExternalWindowType) {
        ExternalWindowType.ORDER_PREVIEW -> {
            val order = payload.getData() as OrderDTO
            OrderPreviewWindow(
                orderDTO = order,
                onClose = { viewModel.closeExternalWindow() },
                onTerminate = { viewModel.terminateOrder(order) },
                onComplete = {
                    val completedOrder = viewModel.completeOrder(it)
                    viewModel.closeExternalWindow()
                    onOrderCompletionCallback(completedOrder)
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
                },
                currency = currency
            )
        }

        else -> Unit
    }
}

private fun mergeProductChanges(
    products: MutableList<ProductDTO>,
    updatedProducts: Collection<ProductDTO>
) {
    val productIdToIndex = products.mapIndexed { index, item -> item.uid to index }.toMap()
    val updatedProductsIdToObjectMap = updatedProducts.associateBy { it.uid }
    productIdToIndex.forEach { (id, index) ->
        updatedProductsIdToObjectMap[id]?.let { updatedValue ->
            products[index] = updatedValue
        }
    }
}

private fun handleLayoutChange(layout: LayoutStyle, viewModel: DefaultViewViewModel) {
    when (layout) {
        LayoutStyle.COMPACT -> {
            swapOrderPreviewToCompactLayout(viewModel)
        }

        else -> {
            // Nothing to swap for now
        }
    }
}

private fun swapOrderPreviewToCompactLayout(viewModel: DefaultViewViewModel) {
    val preview = viewModel.getPreview().value
    if (preview is OrderDetailsPreview) {
        val order = preview.getData().data
        viewModel.setActiveWindow(
            dialogType = ExternalWindowType.ORDER_PREVIEW,
            OrderWindowPayload(order)
        )
        viewModel.setPreview<Unit>(null)
    }
}