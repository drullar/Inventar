package io.drullar.inventar.ui.components.screen.products

import androidx.lifecycle.ViewModel
import io.drullar.inventar.service.ProductsService
import io.drullar.inventar.shared.ProductDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

//data class ProductScreenState TODO encapsulate the exposed properties in a data class ... or split the different states to context appropriate data classes

class ProductViewModel : ViewModel() {

    private val productsService = ProductsService()
    private val _products = MutableStateFlow(
        mutableListOf(*productsService.getAll().toTypedArray())
    )
    private var _selectedProduct = MutableStateFlow<ProductDTO?>(null)
    private var _selectedProductIndex: Int? = null
    private var _selectedProductHasChanges = MutableStateFlow(false)
    private var _showUnsavedChangesAlert = MutableStateFlow(false)
    private var _showNewProductDialog = MutableStateFlow(false)

    val products = _products.asStateFlow()
    val selectedProductHasChanges = _selectedProductHasChanges.asStateFlow()
    val selectedProduct = _selectedProduct.asStateFlow()
    val showUnsavedChangesAlert = _showUnsavedChangesAlert.asStateFlow()
    var showNewProductDialog = _showNewProductDialog.asStateFlow()


    fun updateProduct(product: ProductDTO) {
        productsService.update(product.uid!!, product)
        _products.value[_selectedProductIndex!!] = product
        _selectedProductHasChanges.value = false
    }

    fun selectProduct(product: ProductDTO?) {
        if (product != null) {
            _selectedProduct.value = product
            _selectedProductIndex = _products.value.indexOf(product)
        }
    }

    fun updateSelectedProductHasChanges(value: Boolean) {
        _selectedProductHasChanges.value = value
    }

    fun updateShowUnsavedChangesAlert(value: Boolean) {
        _showUnsavedChangesAlert.value = value
    }

    fun updateshowNewProductDialog(value: Boolean) {
        _showNewProductDialog.value = value
    }
}