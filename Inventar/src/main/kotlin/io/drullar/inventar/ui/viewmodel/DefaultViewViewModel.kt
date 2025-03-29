package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.SortingOrder
import io.drullar.inventar.logging.LoggerImpl
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.persistence.repositories.ProductsRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.viewmodel.delegates.AlertManager
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.viewmodel.delegates.SharedAppStateDelegate
import io.drullar.inventar.ui.data.AlertType
import io.drullar.inventar.ui.data.DetailedProductPreview
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.data.OrderWindowPayload
import io.drullar.inventar.ui.data.OrdersListPreview
import io.drullar.inventar.ui.data.ProductPayload
import io.drullar.inventar.ui.exceptions.ControlledException
import io.drullar.inventar.ui.provider.getLayoutStyle
import io.drullar.inventar.ui.style.LayoutStyle
import io.drullar.inventar.ui.viewmodel.delegates.SettingsProvider
import io.drullar.inventar.ui.viewmodel.delegates.WindowManagerFacade
import io.drullar.inventar.ui.viewmodel.delegates.impl.WindowManagerFacadeImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * View-Model used by [DefaultView] to persist UI state and handle business logic
 */
class DefaultViewViewModel(
    sharedAppStateDelegate: SharedAppStateDelegate,
    alertManagerDelegate: AlertManager,
    settingsProvider: SettingsProvider,
    windowManager: WindowManagerFacade = WindowManagerFacadeImpl(),
    private val productsRepository: ProductsRepository = ProductsRepository,
    private val ordersRepository: OrderRepository = OrderRepository,
) : SharedAppStateDelegate by sharedAppStateDelegate,
    AlertManager by alertManagerDelegate,
    SettingsProvider by settingsProvider,
    WindowManagerFacade by windowManager {

    private val _previewChangeIsAllowed = MutableStateFlow(true)
    val previewChangeIsAllowed = _previewChangeIsAllowed.asStateFlow()

    //TODO review and refactor product state vars
    private val _products = MutableStateFlow(
        productsRepository.getAll().getOrNull()?.toMutableList()
    )
    val products = _products.asStateFlow()


    private val selectedProductId = MutableStateFlow<Int?>(null)
    val _selectedProductId = selectedProductId.asStateFlow()

    private var _selectedProductIndex: Int? = null

    private val sortingOrder = MutableStateFlow(SortingOrder.ASCENDING)
    val _sortingOrder = sortingOrder.asStateFlow()

    private val sortBy = MutableStateFlow(ProductsRepository.SortBy.NAME)
    val _sortBy = sortBy.asStateFlow()

    var preview = getPreview().asStateFlow()

    private val _draftOrdersCount by lazy {
        MutableStateFlow(
            ordersRepository.getCountByStatus(OrderStatus.DRAFT)
        )
    }
    val draftOrdersCount by lazy { _draftOrdersCount.asStateFlow() }

    fun updateProduct(product: ProductDTO): ProductDTO {
        _previewChangeIsAllowed.value = true
        return productsRepository.update(product.uid, product.toProductCreationDTO()).getOrThrow()
    }

    fun selectProduct(product: ProductDTO) {
        if (_previewChangeIsAllowed.value) {
            setPreview(DetailedProductPreview(product))
            selectedProductId.value = product.uid
        }
    }

    fun allowPreviewChange(doAllow: Boolean) {
        _previewChangeIsAllowed.value = doAllow
    }

    fun saveProduct(product: ProductCreationDTO): ProductDTO {
        return productsRepository.save(product).getOrThrow()
    }

    fun showDraftOrders() {
        if (_previewChangeIsAllowed.value) {
            val draftOrders = ordersRepository.getAllByStatus(OrderStatus.DRAFT);
            if (draftOrders.isFailure) throw draftOrders.exceptionOrNull()!!
            else setPreview(OrdersListPreview(draftOrders.getOrThrow()))
        }
    }

    fun showAddProductToOrderDialog(product: ProductDTO) {
        setActiveDialog(DialogWindowType.ADD_PRODUCT_TO_ORDER, ProductPayload(product))
    }

    fun addProductToOrder(
        product: ProductDTO,
        quantity: Int
    ) {
        // If current preview is not OrderCreation and _preview change is not allowed
        val isChangeAllowed = validatePreviewChange { getPreview().value !is OrderDetailsPreview }
        if (!isChangeAllowed) return
        var createdNewOrder = false
        val activeOrder: OrderDTO? = if (getLayoutStyle() == LayoutStyle.COMPACT)
            getSelectedOrderFromCompactLayout()
        else
            getSelectedOrderFromNormalLayout()

        val order: OrderDTO = if (activeOrder != null) ordersRepository.update(
            activeOrder.orderId,
            activeOrder.copy(
                productToQuantity = activeOrder.productToQuantity.toMutableMap()
                    .also { it[product] = quantity }).toOrderCreationDTO()
        ).getOrThrow()
        else ordersRepository.save(
            OrderCreationDTO(
                status = OrderStatus.DRAFT,
                productToQuantity = mutableMapOf(product to quantity)
            )
        ).getOrThrow()!!.also { createdNewOrder = true }

        if (getLayoutStyle() == LayoutStyle.COMPACT)
            setActiveWindow(ExternalWindowType.ORDER_PREVIEW, OrderWindowPayload(order))
        else setPreview(OrderDetailsPreview(order))

        if (createdNewOrder) {
            _draftOrdersCount.value += 1
        }
    }

    private fun getSelectedOrderFromCompactLayout(): OrderDTO? {
        if (getActiveWindow().value != ExternalWindowType.ORDER_PREVIEW) return null
        val activeOrder = getActiveWindowPayload<OrderDTO>().value.getData()
        return if (activeOrder.status != OrderStatus.DRAFT) null else activeOrder
    }

    private fun getSelectedOrderFromNormalLayout(): OrderDTO? {
        if (getPreview().value !is OrderDetailsPreview) return null
        val activeOrder = getPreview().value!!.getPreviewData() as OrderDTO
        return if (activeOrder.status != OrderStatus.DRAFT) null else activeOrder
    }

    fun selectOrder(orderDTO: OrderDTO) {
        val canChangePreview = validatePreviewChange { getPreview().value is OrdersListPreview }
        if (getLayoutStyle() == LayoutStyle.COMPACT) {
            setActiveWindow(ExternalWindowType.ORDER_PREVIEW, OrderWindowPayload(orderDTO))
        } else if (canChangePreview) {
            getPreview().value = OrderDetailsPreview(orderDTO)
        }
    }

    fun completeOrder(orderDTO: OrderDTO) {
        ordersRepository.update(
            orderDTO.orderId,
            orderDTO.copy(status = OrderStatus.COMPLETED).toOrderCreationDTO()
        )
        _draftOrdersCount.value -= 1

        when (getPreview().value) {
            is OrdersListPreview -> {
                val draftOrders =
                    (getPreview().value as OrdersListPreview).getPreviewData().toMutableList()
                draftOrders.remove(orderDTO)

                getPreview().value = OrdersListPreview(
                    ordersRepository.getAllByStatus(OrderStatus.DRAFT).getOrThrow()
                )
            }

            is OrderDetailsPreview -> {
                val order = (getPreview().value as OrderDetailsPreview).getPreviewData()
            }
        }
    }

    fun removeProductFromOrder(product: ProductDTO, order: OrderDTO) {
        try {
            val updatedProductsMap =
                order.productToQuantity.toMutableMap().also { it.remove(product) }
            val updatedOrder = ordersRepository.update(
                order.orderId,
                order.copy(productToQuantity = updatedProductsMap).toOrderCreationDTO()
            )
            setPreview(OrderDetailsPreview(updatedOrder.getOrThrow()))
        } catch (e: ControlledException) {
            e.message?.let { log.error(it) }
        }
    }

    fun changeProductQuantityInOrder(product: ProductDTO, newQuantity: Int) {
        val layoutStyle = getLayoutStyle()
        val order: OrderDTO
        var doUpdatePreview = false
        var doUpdateWindow = false

        if (layoutStyle == LayoutStyle.NORMAL && getPreview().value is OrderDetailsPreview) {
            order = (getPreview().value as OrderDetailsPreview).getPreviewData()
            doUpdatePreview = true
        } else if (layoutStyle == LayoutStyle.COMPACT && getActiveWindow().value == ExternalWindowType.ORDER_PREVIEW) {
            order = getActiveWindowPayload<OrderDTO>().value.getData()
            doUpdateWindow = true
        } else return

        val productsMap = order.productToQuantity.toMutableMap()
        productsMap[product] = newQuantity
        val updatedOrder = ordersRepository.update(
            order.orderId,
            order.copy(productToQuantity = productsMap).toOrderCreationDTO()
        ).getOrThrow()

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
        setActiveDialog<Unit>(null, EmptyPayload())
    }

    fun closeExternalWindow() {
        setActiveWindow<Unit>(null, EmptyPayload())
    }

    fun getCurrentOrderTargetProductQuantity(product: ProductDTO): Int? {
        val activeOrder = getSelectedOrderFromNormalLayout() ?: getSelectedOrderFromCompactLayout()
        return activeOrder?.let { it.productToQuantity[product] }
    }

    fun fetchProducts(
        page: Int,
        pageSize: Int,
        sortBy: ProductsRepository.SortBy,
        order: SortingOrder
    ) = productsRepository.getPaged(page, pageSize, sortBy, order).getOrThrow()

    private fun isProductBeingEdited(product: ProductDTO) =
        getPreview().value is DetailedProductPreview &&
                (getPreview().value as DetailedProductPreview).getPreviewData().uid == product.uid &&
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

    companion object {
        private val log = LoggerImpl(this::class)
    }
}