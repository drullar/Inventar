package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.logging.LoggerImpl
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.persistence.repositories.ProductsRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.viewmodel.delegates.AlertManager
import io.drullar.inventar.ui.viewmodel.delegates.DialogManager
import io.drullar.inventar.ui.data.DialogType
import io.drullar.inventar.ui.viewmodel.delegates.SharedAppStateDelegate
import io.drullar.inventar.ui.viewmodel.delegates.impl.DialogManagerImpl
import io.drullar.inventar.ui.data.AlertType
import io.drullar.inventar.ui.data.DetailedProductPreview
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.data.OrdersListPreview
import io.drullar.inventar.ui.exceptions.ControlledException
import io.drullar.inventar.ui.viewmodel.delegates.SettingsProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * View-Model used by [DefaultView] to persist UI state and handle business logic
 */
class DefaultViewViewModel(
    sharedAppStateDelegate: SharedAppStateDelegate,
    alertManagerDelegate: AlertManager,
    settingsProvider: SettingsProvider,
    dialogManager: DialogManager = DialogManagerImpl(),
    private val productsRepository: ProductsRepository = ProductsRepository,
    private val ordersRepository: OrderRepository = OrderRepository,
) : SharedAppStateDelegate by sharedAppStateDelegate,
    AlertManager by alertManagerDelegate,
    DialogManager by dialogManager,
    SettingsProvider by settingsProvider {

    private val _previewChangeIsAllowed = MutableStateFlow(true)
    val previewChangeIsAllowed = _previewChangeIsAllowed.asStateFlow()

    private val _products = MutableStateFlow(
        productsRepository.getAll().getOrNull()?.toMutableList()
    )
    val products = _products.asStateFlow()

    private var _selectedProductIndex: Int? = null

    var preview = getPreview().asStateFlow()

    var targetProduct = MutableStateFlow<ProductDTO?>(null)

    private val _draftOrdersCount by lazy {
        MutableStateFlow(
            ordersRepository.getCountByStatus(OrderStatus.DRAFT)
        )
    }
    val draftOrdersCount by lazy { _draftOrdersCount.asStateFlow() }

    fun updateProduct(product: ProductDTO) {
        productsRepository.update(product.uid, product.toProductCreationDTO())
        _products.value?.set(_selectedProductIndex!!, product)
        _previewChangeIsAllowed.value = true
    }

    fun selectProduct(product: ProductDTO) {
        if (_previewChangeIsAllowed.value) {
            setPreview(DetailedProductPreview(product))
            _selectedProductIndex = _products.value?.indexOf(product)
        }
    }

    fun allowPreviewChange(doAllow: Boolean) {
        _previewChangeIsAllowed.value = doAllow
    }

    fun addNewProduct(product: ProductCreationDTO) {
        val persistedObject: ProductDTO =
            productsRepository.save(product).getOrNull()!!
        _products.value = (_products.value?.plus(persistedObject))?.toMutableList()
    }

    fun showDraftOrders() {
        if (_previewChangeIsAllowed.value) {
            val draftOrders = ordersRepository.getAllByStatus(OrderStatus.DRAFT);
            if (draftOrders.isFailure) throw draftOrders.exceptionOrNull()!!
            else setPreview(OrdersListPreview(draftOrders.getOrNull()!!))
        }
    }

    fun showOrderProductDialog(product: ProductDTO) {
        targetProduct.value = product
        setActiveDialog(DialogType.ADD_PRODUCT_TO_ORDER)
    }

    fun addProductToOrder(quantity: Int) {
        val order: OrderDTO
        try {
            // If current preview is not OrderCreation and _preview change is not allowed
            val isAllowed = validatePreviewChange { getPreview().value !is OrderDetailsPreview }
            if (!isAllowed) return
            // If OrderCreation is already selected
            if (getPreview().value is OrderDetailsPreview) {
                order = (getPreview().value as OrderDetailsPreview).getPreviewData()
                if (order.status != OrderStatus.DRAFT) { // Currently viewed order is in a unchangeable state
                    val newOrder = ordersRepository.save(
                        OrderCreationDTO(
                            status = OrderStatus.DRAFT,
                            productToQuantity = mutableMapOf(targetProduct.value!! to quantity)
                        )
                    ).getOrThrow()!!
                    getPreview().value = OrderDetailsPreview(newOrder)
                } else {
                    val updatedProductsMap = order.productToQuantity.toMutableMap()
                        .also { it[targetProduct.value!!] = quantity }

                    val updatedOrder = ordersRepository
                        .update(
                            order.orderId,
                            order.copy(productToQuantity = updatedProductsMap).toOrderCreationDTO()
                        )
                        .getOrThrow()
                    getPreview().value = OrderDetailsPreview(updatedOrder)
                }
            } else {
                order = ordersRepository.save(
                    OrderCreationDTO(
                        status = OrderStatus.DRAFT,
                        productToQuantity = mutableMapOf(targetProduct.value!! to quantity)
                    )
                ).getOrThrow()!!
                getPreview().value = OrderDetailsPreview(order)
                _draftOrdersCount.value += 1
            }
        } catch (exception: ControlledException) {
            setActiveDialog(null) // TODO Exception window
            exception.message?.let { log.error(it) }
        }
    }

    fun selectOrder(orderDTO: OrderDTO) {
        validatePreviewChange { getPreview().value is OrdersListPreview }
        getPreview().value = OrderDetailsPreview(orderDTO)
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

    fun removeProductFromOrder(product: ProductDTO) {
        try {
            val order = (getPreview().value as OrderDetailsPreview).getPreviewData()
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
        if (getPreview().value is OrderDetailsPreview) {
            val order = (getPreview().value as OrderDetailsPreview).getPreviewData()
            val productsMap = order.productToQuantity.toMutableMap()
            productsMap[product] = newQuantity
            setPreview(OrderDetailsPreview(order.copy(productToQuantity = productsMap.toMap())))
        }
    }

    fun deleteProduct(product: ProductDTO) {
        // Whether the product queued for deletion is being edited
        if (isProductBeingEdited(product)) {
            // TODO set show alert dialog
        } else {
            _products.value = (_products.value!! - product).toMutableList()
            productsRepository.deleteById(product.uid)
        }
        //TODO validate whether the product is in any Draft order
    }

    fun getCurrentOrderTargetProductQuantity(): Int? =
        if (preview.value is OrderDetailsPreview) {
            (preview.value as OrderDetailsPreview).getPreviewData()
                .productToQuantity.getOrDefault(
                    targetProduct.value, null
                )
        } else null

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