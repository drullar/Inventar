package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.persistence.repositories.impl.ProductsRepository
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.ui.viewmodel.delegate.AlertManager
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.viewmodel.delegate.SharedAppStateDelegate
import io.drullar.inventar.ui.data.AlertType
import io.drullar.inventar.ui.data.DetailedProductPreview
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.data.OrderWindowPayload
import io.drullar.inventar.ui.data.OrdersListPreview
import io.drullar.inventar.ui.data.ProductPayload
import io.drullar.inventar.ui.provider.getLayoutStyle
import io.drullar.inventar.ui.style.LayoutStyle
import io.drullar.inventar.ui.viewmodel.delegate.OrdersDelegate
import io.drullar.inventar.ui.viewmodel.delegate.SettingsProvider
import io.drullar.inventar.ui.viewmodel.delegate.WindowManagerFacade
import io.drullar.inventar.ui.viewmodel.delegate.impl.BarcodeScanManager
import io.drullar.inventar.ui.viewmodel.delegate.impl.BarcodeScanManagerInterface
import io.drullar.inventar.ui.viewmodel.delegate.impl.WindowManagerFacadeImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * View-Model used by [DefaultView] to persist UI state and handle business logic
 */
class DefaultViewViewModel(
    sharedAppStateDelegate: SharedAppStateDelegate,
    alertManagerDelegate: AlertManager,
    settingsProvider: SettingsProvider,
    barcodeScanManager: BarcodeScanManager,
    private val ordersDelegate: OrdersDelegate,
    windowManager: WindowManagerFacade = WindowManagerFacadeImpl(),
    private val productsRepository: ProductsRepository = ProductsRepository,
) : SharedAppStateDelegate by sharedAppStateDelegate,
    AlertManager by alertManagerDelegate,
    SettingsProvider by settingsProvider,
    WindowManagerFacade by windowManager,
    BarcodeScanManagerInterface by barcodeScanManager,
    OrdersDelegate by ordersDelegate {

    private val _previewChangeIsAllowed = MutableStateFlow(true)
    val previewChangeIsAllowed = _previewChangeIsAllowed.asStateFlow()

    private val sortingOrder = MutableStateFlow(SortingOrder.ASCENDING)
    val _sortingOrder = sortingOrder.asStateFlow()

    private val sortBy = MutableStateFlow(ProductsRepository.SortBy.NAME)
    val _sortBy = sortBy.asStateFlow()
    var preview = getPreview().asStateFlow()

    fun updateProduct(product: ProductDTO): ProductDTO {
        _previewChangeIsAllowed.value = true
        return productsRepository.update(product.uid, product.toProductCreationDTO()).getOrThrow()
    }

    fun selectProduct(product: ProductDTO) {
        setPreview(DetailedProductPreview(product)) // TODO prevent new product selection when current product has changes
    }

    fun allowPreviewChange(doAllow: Boolean) {
        _previewChangeIsAllowed.value = doAllow
    }

    fun saveProduct(product: ProductCreationDTO): ProductDTO {
        val cleanedUpProduct = product.copy(name = product.name.trim())
        return productsRepository.save(cleanedUpProduct).getOrThrow()
    }

    fun showDraftOrders() {
        setPreview(OrdersListPreview(getAllByStatus(OrderStatus.DRAFT)))
    }

    fun showAddProductToOrderDialog(product: ProductDTO) {
        setActiveDialog(DialogWindowType.ADD_PRODUCT_TO_ORDER, ProductPayload(product))
    }

    fun showChangeProductQuantity(product: ProductDTO) {
        setActiveDialog(DialogWindowType.CHANGE_PRODUCT_QUANTITY, ProductPayload(product))
    }

    fun addProductToOrder(
        product: ProductDTO,
        quantity: Int
    ) {
        val activeOrder: OrderDTO? =
            if (getLayoutStyle() == LayoutStyle.COMPACT) getSelectedOrderFromCompactLayout()
            else getSelectedOrderFromNormalLayout()

        val currentProductOrderQuantity = activeOrder?.let { it.productToQuantity[product] } ?: 0

        val order: OrderDTO =
            if (activeOrder != null) updateProductsQuantity(
                activeOrder,
                mapOf(product to quantity + currentProductOrderQuantity)
            )
            else createOrder(mapOf(product to quantity))

        if (getLayoutStyle() == LayoutStyle.COMPACT)
            setActiveWindow(ExternalWindowType.ORDER_PREVIEW, OrderWindowPayload(order))
        else setPreview(OrderDetailsPreview(order))
    }

    private fun getSelectedOrderFromCompactLayout(): OrderDTO? {
        if (getActiveWindow().value != ExternalWindowType.ORDER_PREVIEW) return null
        val activeOrder = getActiveWindowPayload<OrderDTO>().value.getData()
        return if (activeOrder.status != OrderStatus.DRAFT) null else activeOrder
    }

    private fun getSelectedOrderFromNormalLayout(): OrderDTO? {
        val preview = getPreview().value
        if (preview !is OrderDetailsPreview) return null
        val activeOrder = preview.getData().data
        return if (activeOrder.status != OrderStatus.DRAFT) null else activeOrder
    }

    override fun terminateOrder(order: OrderDTO): OrderDTO {
        val updatedOrder = ordersDelegate.terminateOrder(order)
        updateOrderInUI(updatedOrder)
        return updatedOrder
    }

    fun selectOrder(orderDTO: OrderDTO) {
        val canChangePreview = validatePreviewChange { getPreview().value is OrdersListPreview }
        if (getLayoutStyle() == LayoutStyle.COMPACT) {
            setActiveWindow(ExternalWindowType.ORDER_PREVIEW, OrderWindowPayload(orderDTO))
        } else if (canChangePreview) {
            getPreview().value = OrderDetailsPreview(orderDTO)
        }
    }

    override fun completeOrder(order: OrderDTO): OrderDTO {
        val updatedOrder = ordersDelegate.completeOrder(order)
        updateOrderInUI(updatedOrder)
        return updatedOrder
    }

    fun removeProductFromOrder(product: ProductDTO, order: OrderDTO) {
        val updatedOrder = updateProductsQuantity(order, mapOf(product to 0))
        setPreview(OrderDetailsPreview(updatedOrder))
    }

    fun changeProductQuantityInOrder(product: ProductDTO, newQuantity: Int) {
        val layoutStyle = getLayoutStyle()
        val order: OrderDTO
        var doUpdatePreview = false
        var doUpdateWindow = false

        if (layoutStyle == LayoutStyle.NORMAL && getPreview().value is OrderDetailsPreview) {
            order = (getPreview().value as OrderDetailsPreview).getData().data
            doUpdatePreview = true
        } else if (layoutStyle == LayoutStyle.COMPACT && getActiveWindow().value == ExternalWindowType.ORDER_PREVIEW) {
            order = getActiveWindowPayload<OrderDTO>().value.getData()
            doUpdateWindow = true
        } else return
        val updatedOrder = updateProductsQuantity(order, mapOf(product to newQuantity))

        if (doUpdatePreview) setPreview(OrderDetailsPreview(updatedOrder))
        if (doUpdateWindow) setActiveWindow(
            ExternalWindowType.ORDER_PREVIEW,
            OrderWindowPayload(updatedOrder)
        )
    }

    fun deleteProduct(product: ProductDTO) {
        // Whether the product queued for deletion is being edited
        if (isProductBeingEdited(product)) {
            // TODO set show alert dialog
        } else {
            productsRepository.deleteById(product.uid)
        }
        //TODO validate whether the product is in any Draft order
    }

    fun closeDialogWindow() {
        setActiveDialog(null, EmptyPayload())
    }

    fun closeExternalWindow() {
        setActiveWindow(null, EmptyPayload())
    }

    fun getCurrentOrderTargetProductQuantity(product: ProductDTO): Int? {
        val activeOrder = getSelectedOrderFromNormalLayout() ?: getSelectedOrderFromCompactLayout()
        return activeOrder?.let { it.productToQuantity[product] }
    }

    fun fetchProducts(
        pageRequest: PagedRequest<ProductsRepository.SortBy>
    ) = productsRepository.getPaged(pageRequest).getOrThrow()

    fun searchProducts(searchQuery: String, pageRequest: PagedRequest<ProductsRepository.SortBy>) =
        productsRepository.search(searchQuery, pageRequest).getOrThrow()

    private fun isProductBeingEdited(product: ProductDTO) =
        getPreview().value is DetailedProductPreview &&
                (getPreview().value as DetailedProductPreview).getData().data.uid == product.uid &&
                !_previewChangeIsAllowed.value

    /**
     * Validate whether preview change should be allowed. Returns whether change is allowed
     */
    private fun validatePreviewChange(additionalValidation: () -> Boolean): Boolean {
        if (!_previewChangeIsAllowed.value && additionalValidation()) {
            setActiveAlert(AlertType.UNSAVED_CHANGES)
            return false
        }
        return true
    }

    /**
     * Update the order in the correct places. After it has been updated in the backend
     */
    private fun updateOrderInUI(updatedOrder: OrderDTO) {
        val activePreview = getPreview().value
        if (getLayoutStyle() == LayoutStyle.COMPACT && getActiveWindow().value == ExternalWindowType.ORDER_PREVIEW) {
            // Order is being viewed in external window
            setActiveWindow(ExternalWindowType.ORDER_PREVIEW, OrderWindowPayload(updatedOrder))
        } else if (activePreview is OrderDetailsPreview) {
            setPreview(OrderDetailsPreview(updatedOrder))
        }
        if (activePreview is OrdersListPreview) {
            setPreview(OrdersListPreview(getAllByStatus(OrderStatus.DRAFT)))
        }
    }
}