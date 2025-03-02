package io.drullar.inventar.ui.components.viewmodel

import io.drullar.inventar.logging.LoggerImpl
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.persistence.repositories.ProductsRepository
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.RepositoryResponse
import io.drullar.inventar.shared.getDataOnSuccessOrNull
import io.drullar.inventar.shared.getOrThrow
import io.drullar.inventar.ui.components.dialog.DialogType
import io.drullar.inventar.ui.components.views.default.DetailedProductPreview
import io.drullar.inventar.ui.components.views.default.OrderCreationPreview
import io.drullar.inventar.ui.components.views.default.OrdersListPreview
import io.drullar.inventar.ui.components.views.default.Preview
import io.drullar.inventar.ui.exceptions.ControlledException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * View-Model used by [DefaultView] to persist UI state and handle business logic
 */
//TODO split some of the responsibilities of the ViewModel to delegates
class DefaultViewViewModel(
    sharedAppStateHolder: SharedAppStateHolder,
    private val productsRepository: ProductsRepository = ProductsRepository,
    private val ordersRepository: OrderRepository = OrderRepository
) : SharedAppStateHolder by sharedAppStateHolder {

    private val _previewChangeIsAllowed = MutableStateFlow(true)
    val previewChangeIsAllowed = _previewChangeIsAllowed.asStateFlow()

    private val _products = MutableStateFlow(
        productsRepository.getAll().getDataOnSuccessOrNull()?.toMutableList()
    )
    val products = _products.asStateFlow()

    private var _selectedProductIndex: Int? = null

    private var _showUnsavedChangesAlert = MutableStateFlow(false)

    private var _dialogToDisplay = MutableStateFlow(DialogType.NONE)
    var dialogToDisplay = _dialogToDisplay.asStateFlow()

    private var _preview = MutableStateFlow<Preview<*>?>(null)
    var preview = _preview.asStateFlow()

    var targetProduct = MutableStateFlow<ProductDTO?>(null)

    private val _draftOrdersCount = MutableStateFlow(
        ordersRepository.getCountByStatus(OrderStatus.DRAFT)
    )
    val draftOrdersCount = _draftOrdersCount.asStateFlow()

    fun updateProduct(product: ProductDTO) {
        productsRepository.update(product.uid, product.toProductCreationDTO())
        _products.value?.set(_selectedProductIndex!!, product)
        _previewChangeIsAllowed.value = true
    }

    fun selectProduct(product: ProductDTO) {
        if (_previewChangeIsAllowed.value) {
            _preview.value = DetailedProductPreview(product)
            _selectedProductIndex = _products.value?.indexOf(product)
        }
    }

    fun allowPreviewChange(doAllow: Boolean) {
        _previewChangeIsAllowed.value = doAllow
    }

    fun showUnsavedChangesAlert() {
        _dialogToDisplay.value = DialogType.UNSAVED_CHANGES_ALERT
    }

    fun showNewProductDialog() {
        _dialogToDisplay.value = DialogType.NEW_PRODUCT
    }

    fun closeCurrentDialog() {
        _dialogToDisplay.value = DialogType.NONE
    }

    fun addNewProduct(product: ProductCreationDTO) {
        val persistedObject: ProductDTO =
            productsRepository.save(product).getDataOnSuccessOrNull()!!
        _products.value = (_products.value?.plus(persistedObject))?.toMutableList()
    }

    fun showDraftOrders() {
        if (_previewChangeIsAllowed.value) {
            val draftOrders = ordersRepository.getAllByStatus(OrderStatus.DRAFT);
            if (draftOrders is RepositoryResponse.Failure) throw (draftOrders as RepositoryResponse.Failure).exception
            else _preview.value = OrdersListPreview(draftOrders.getDataOnSuccessOrNull()!!)
        }
    }

    fun showOrderProductDialog(product: ProductDTO) {
        targetProduct.value = product
        _dialogToDisplay.value = DialogType.ADD_PRODUCT_TO_ORDER
    }

    fun addProductToOrder(quantity: Int) {
        // TODO handle case where product is already in the order. Show to the user a prompt to modify the already added amount
        val order: OrderDTO
        try {
            // If current preview is not OrderCreation and _preview change is not allowed
            val isAllowed = validatePreviewChange { _preview.value !is OrderCreationPreview }
            if (!isAllowed) return
            // If OrderCreation is already selected
            if (_preview.value is OrderCreationPreview) {
                order = (_preview.value as OrderCreationPreview).getPreviewData()
                val updatedProductsMap = order.productToQuantity.toMutableMap()
                    .also { it[targetProduct.value!!] = quantity }

                val updatedOrder = ordersRepository
                    .update(
                        order.orderId,
                        order.copy(productToQuantity = updatedProductsMap).toOrderCreationDTO()
                    )
                    .getOrThrow()
                _preview.value = OrderCreationPreview(updatedOrder)
            } else {
                order = ordersRepository.save(
                    OrderCreationDTO(
                        status = OrderStatus.DRAFT,
                        productToQuantity = mutableMapOf(targetProduct.value!! to quantity)
                    )
                ).getOrThrow()
                _preview.value = OrderCreationPreview(order)
                _draftOrdersCount.value += 1
            }
        } catch (exception: ControlledException) {
            closeCurrentDialog()
            exception.message?.let { log.error(it) }
        }
    }

    fun selectOrder(orderDTO: OrderDTO) {
        validatePreviewChange { _preview.value is OrdersListPreview }
        _preview.value = OrderCreationPreview(orderDTO)
    }

    fun completeOrder(orderDTO: OrderDTO) {
        ordersRepository.update(
            orderDTO.orderId,
            orderDTO.copy(status = OrderStatus.COMPLETED).toOrderCreationDTO()
        )
        _draftOrdersCount.value -= 1

        when (_preview.value) {
            is OrdersListPreview -> {
                val draftOrders =
                    (_preview.value as OrdersListPreview).getPreviewData().toMutableList()
                draftOrders.remove(orderDTO)

                _preview.value = OrdersListPreview(
                    ordersRepository.getAllByStatus(OrderStatus.DRAFT).getOrThrow()
                )
            }

            is OrderCreationPreview -> {
                val order = (_preview.value as OrderCreationPreview).getPreviewData()
            }
        }


    }

    fun removeProductFromOrder(product: ProductDTO) {
        try {
            val order = (_preview.value as OrderCreationPreview).getPreviewData()
            val updatedProductsMap =
                order.productToQuantity.toMutableMap().also { it.remove(product) }
            val updatedOrder = ordersRepository.update(
                order.orderId,
                order.copy(productToQuantity = updatedProductsMap).toOrderCreationDTO()
            )
            _preview.value = OrderCreationPreview(updatedOrder.getOrThrow())
        } catch (e: ControlledException) {
            e.message?.let { log.error(it) }
        }
    }

    fun changeProductQuantityInOrder(product: ProductDTO, newQuantity: Int) {
        if (_preview.value is OrderCreationPreview) {
            val order = (_preview.value as OrderCreationPreview).getPreviewData()
            val productsMap = order.productToQuantity.toMutableMap()
            productsMap[product] = newQuantity
            _preview.value =
                OrderCreationPreview(order.copy(productToQuantity = productsMap.toMap()))
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

    private fun isProductBeingEdited(product: ProductDTO) =
        _preview.value is DetailedProductPreview &&
                (_preview.value as DetailedProductPreview).getPreviewData().uid == product.uid &&
                !_previewChangeIsAllowed.value

    private fun validateProductQuantity(quantity: Int) {
        val product =
            productsRepository.getById(targetProduct.value!!.uid).getDataOnSuccessOrNull()!!
        if (product.availableQuantity < quantity) {
            throw ControlledException(
                "There isn't enough from this product to complete the operation. " +
                        "Required amount: ${quantity}. Available amount: ${product.availableQuantity}. " +
                        "Try again with an amount lower than or equal to ${product.availableQuantity} or " +
                        "modify available amount of \"${product.name}\""
            )
        }
    }

    /**
     * Validate whether preview change should be allowed. Returns whether change is allowed
     */
    private fun validatePreviewChange(additionalValidation: () -> Boolean): Boolean {
        if (!_previewChangeIsAllowed.value && additionalValidation()) {
            showUnsavedChangesAlert()
            return false
        }
        return true
    }

    companion object {
        private val log = LoggerImpl(this::class)
    }
}