package io.drullar.inventar.ui.components.views.default

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
import androidx.compose.runtime.snapshotFlow
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
import io.drullar.inventar.ui.components.cards.OrderCreationCard
import io.drullar.inventar.ui.components.cards.OrdersList
import io.drullar.inventar.ui.components.cards.EditProductCard
import io.drullar.inventar.ui.components.cards.ProductSummarizedPreviewCard
import io.drullar.inventar.ui.components.window.dialog.NewProductDialog
import io.drullar.inventar.ui.components.window.external.OrderCreationWindow
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.components.window.dialog.ChangeProductQuantityDialog
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.components.views.default.layout.DraftOrderButton
import io.drullar.inventar.ui.components.views.default.layout.ProductUtilBar
import io.drullar.inventar.ui.components.window.dialog.AlertDialog
import io.drullar.inventar.ui.components.window.dialog.OrderProductConfirmationDialog
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
import kotlinx.coroutines.flow.drop
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
    val draftOrdersCount by viewModel.getDraftOrdersCount().collectAsState()
    val preview by viewModel.preview.collectAsState()
    val settings by viewModel.getSettings().collectAsState()

    var page by remember { mutableStateOf(1) }
    val sortBy by viewModel._sortBy.collectAsState()
    val sortingOrder by viewModel._sortingOrder.collectAsState()
    var totalProducts by remember { mutableStateOf(0) }
    val products = remember {
        mutableStateListOf<ProductDTO>().apply {
            addAll(
                viewModel.fetchProducts(
                    PagedRequest(page, PRODUCTS_PER_PAGE, sortingOrder, sortBy)
                ).let {
                    totalProducts = it.totalItems.toInt()
                    it.items
                }
            )
        }
    }

    // Handle switching between Normal and Compact layout
    handleLayoutStyleChange(layout, viewModel)

    // Lambdas
    val onProductSelectCallback = remember {
        { product: ProductDTO ->
            viewModel.selectProduct(
                product
            )
        }
    }

    val onProductDeleteCallback: (ProductDTO) -> Unit = remember {
        { product: ProductDTO ->
            viewModel.deleteProduct(product)
            products.remove(product)
        }
    }

    val onAddProductToOrderLambda = remember {
        { product: ProductDTO ->
            viewModel.showAddProductToOrderDialog(product)
        }
    }

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
                draftOrdersCount = draftOrdersCount,
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

                        if (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == products.size - 1
                            && totalProducts > products.size
                        ) {
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
                            productData = product,
                            currency = settings.defaultCurrency,
                            onClickCallback = onProductSelectCallback,
                            locale = settings.language.locale,
                            onEditRequest = onProductSelectCallback,
                            onDeleteRequest = onProductDeleteCallback,
                            onAddToOrderRequest = onAddProductToOrderLambda
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
                        EditProductCard(
                            productData = product,
                            onRevert = { product },
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
                        OrderCreationCard(
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
                            currency = settings.defaultCurrency,
                            locale = settings.language.locale
                        )
                    }

                    is OrdersListPreview -> {
                        val draftOrders = (preview as OrdersListPreview).getData().data
                        OrdersList(
                            orders = draftOrders,
                            style = layout,
                            activeLocale = settings.language.locale,
                            onComplete = { hasProblems, order ->
                                if (!hasProblems) viewModel.completeOrder(order)
                                else viewModel.setActiveDialog(
                                    DialogWindowType.ORDER_QUANTITY_ISSUES_ALERT,
                                    OrderWindowPayload(order)
                                )
                            },
                            onSelect = { selectedOrder ->
                                viewModel.selectOrder(selectedOrder)
                            },
                            onTerminate = { terminatedOrder ->
                                viewModel.terminateOrder(terminatedOrder)
                            },
                            currency = settings.defaultCurrency,
                            validateProductAvailability = { order ->
                                viewModel.validateProductsAvailability(
                                    order
                                )
                            }
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
        },
        onChangeProductQuantity = { oldProduct, newProduct ->
            products[products.indexOf(oldProduct)] = newProduct
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
    onAddNewProduct: (ProductCreationDTO) -> Unit,
    /**
     * first argument - oldProduct
     * second arguments - updatedProduct
     */
    onChangeProductQuantity: (ProductDTO, ProductDTO) -> Unit
) {
    when (activeDialogWindow) {
        DialogWindowType.NEW_PRODUCT -> {
            val barcodePayload = try {
                viewModel.getActiveDialogPayload<String>().value as BarcodePayload
            } catch (e: ClassCastException) {
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
            OrderProductConfirmationDialog(
                product = product,
                initialQuantity = 1,
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
                    val updatedProduct =
                        viewModel.updateProduct(product.copy(availableQuantity = newQuantity))
                    viewModel.setActiveDialog(null, EmptyPayload())
                    onChangeProductQuantity(product, updatedProduct)
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
    val payload by viewModel.getActiveWindowPayload<Any>().collectAsState()

    when (activeExternalWindowType) {
        ExternalWindowType.ORDER_PREVIEW -> {
            val order = payload.getData() as OrderDTO
            OrderCreationWindow(
                orderDTO = order,
                onClose = { viewModel.closeExternalWindow() },
                onTerminate = { viewModel.terminateOrder(order) },
                onComplete = { hasProblems, completedOrder ->
                    if (!hasProblems) {
                        val updatedOrder = viewModel.completeOrder(completedOrder)
                        viewModel.closeExternalWindow()
                        onOrderCompletionCallback(updatedOrder)
                    } else {
                        viewModel.setActiveDialog(
                            DialogWindowType.ORDER_QUANTITY_ISSUES_ALERT,
                            OrderWindowPayload(completedOrder)
                        )
                    }
                },
                onProductValueChange = { product, quantity ->
                    viewModel.changeProductQuantityInOrder(
                        product,
                        quantity
                    )
                },
                onProductRemove = { product, targetOrder ->
                    viewModel.removeProductFromOrder(
                        product,
                        targetOrder
                    )
                },
                currency = currency,
                barcodeScanManager = viewModel,
                validateProductAvailability = {
                    viewModel.validateProductsAvailability(
                        it
                    )
                },
                locale = viewModel.getSettings().value.language.locale
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

private fun handleLayoutStyleChange(layout: LayoutStyle, viewModel: DefaultViewViewModel) {
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