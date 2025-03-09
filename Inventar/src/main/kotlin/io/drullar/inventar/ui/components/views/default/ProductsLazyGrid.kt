package io.drullar.inventar.ui.components.views.default

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.ProductSummarizedPreviewCard
import java.util.Currency

@Composable
fun ProductsLazyGrid(
    products: List<ProductDTO>,
    currency: Currency,
    onProductSelectCallback: (ProductDTO) -> Unit,
    selectionIsAllowed: Boolean,
    onProductDeleteRequest: (ProductDTO) -> Unit,
    onProductEditRequest: (ProductDTO) -> Unit,
    onAddProductToOrderRequest: (ProductDTO) -> Unit
) {
    val selectedProduct by remember { mutableStateOf<ProductDTO?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(products) { productData ->
            ProductSummarizedPreviewCard(
                productData,
                currency = currency,
                onClickCallback = onProductSelectCallback,
                isSelected = productData == selectedProduct,
                selectionIsAllowed = selectionIsAllowed,
                onEditRequest = onProductEditRequest,
                onDeleteRequest = onProductDeleteRequest,
                onAddToOrderRequest = onAddProductToOrderRequest
            )
        }
    }
}