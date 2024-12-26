package io.drullar.inventar.ui.components.views.products

import androidx.lifecycle.ViewModel
import io.drullar.inventar.persistence.model.Order
import io.drullar.inventar.service.OrdersService
import io.drullar.inventar.service.ProductsService
import io.drullar.inventar.shared.ProductDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductViewModel : ViewModel() {
    private val productsService = ProductsService()
    private val ordersService = OrdersService()

    private val _previewChangeIsAllowed = MutableStateFlow(true)
    val previewChangeIsAllowed = _previewChangeIsAllowed.asStateFlow()

    private val _products = MutableStateFlow(productsService.getAll().toMutableList())
    val products = _products.asStateFlow()

    private var _selectedProductIndex: Int? = null

    private var _showUnsavedChangesAlert = MutableStateFlow(false)
    val showUnsavedChangesAlert = _showUnsavedChangesAlert.asStateFlow()

    private var _showNewProductDialog = MutableStateFlow(false)
    var showNewProductDialog = _showNewProductDialog.asStateFlow()

    private var _preview = MutableStateFlow<Preview<*>?>(null)
    var preview = _preview.asStateFlow()

    private val _draftOrders by lazy { MutableStateFlow(ordersService.getDraftOrders()) }
    val draftOrders by lazy { _draftOrders.asStateFlow() }

    private val _orderButtonText = MutableStateFlow(
        _draftOrders.value.count().let {
            when {
                it == 0 -> ""
                it > 99 -> "99+"
                else -> it.toString()
            }
        }
    )
    val orderButtonCount = _orderButtonText.asStateFlow()

    fun updateProduct(product: ProductDTO) {
        productsService.update(product.uid!!, product)
        _products.value[_selectedProductIndex!!] = product
        _previewChangeIsAllowed.value = true
    }

    fun selectProduct(product: ProductDTO) {
        if (_previewChangeIsAllowed.value) {
            _preview.value = DetailedProductPreview(product)
            _selectedProductIndex = _products.value.indexOf(product)
        }
    }

    fun allowPreviewChange() {
        _previewChangeIsAllowed.value = true
    }

    fun forbidPreviewChange() {
        _previewChangeIsAllowed.value = false
    }

    fun updateShowUnsavedChangesAlert(value: Boolean) {
        _showUnsavedChangesAlert.value = value
    }

    fun updateShowNewProductDialog(value: Boolean) {
        _showNewProductDialog.value = value
    }

    fun addNewProduct(product: ProductDTO) {
        val persistedObject: ProductDTO = productsService.save(product)
        _products.value = (_products.value + persistedObject).toMutableList()
    }

    fun handleOrdersButtonClick(): List<Order>? {
        if (_previewChangeIsAllowed.value) {
            return _draftOrders.value;
        }
        return null
    }

    fun deleteProduct(product: ProductDTO) {
        // Whether the product queued for deletion is being edited
        if (isProductBeingEdited(product)) {
            // TODO set show alert dialog
        } else {
            _products.value = (_products.value - product).toMutableList()
            productsService.delete(product.uid!!)
        }
    }

    fun handleAddProductToOrder() {
        throw NotImplementedError()
    }

    private fun isProductBeingEdited(product: ProductDTO) =
        _preview.value is DetailedProductPreview &&
                (_preview.value as DetailedProductPreview).getPreviewData().uid == product.uid &&
                !_previewChangeIsAllowed.value
}