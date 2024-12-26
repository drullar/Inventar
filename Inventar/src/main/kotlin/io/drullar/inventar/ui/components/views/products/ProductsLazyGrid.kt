package io.drullar.inventar.ui.components.views.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.ProductSummarizedPreviewCard

@Composable
fun ProductsLazyGrid(
    products: List<ProductDTO>,
    onProductSelectCallback: (ProductDTO) -> Unit,
    selectionIsAllowed: Boolean,
    onProductDeleteRequest: (ProductDTO) -> Unit,
    onProductEditRequest: (ProductDTO) -> Unit,
    onAddProductToOrderRequest: (ProductDTO) -> Unit
) {
    val selectedProduct by remember { mutableStateOf<ProductDTO?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(products) { productData ->
            Box(modifier = Modifier.padding(5.dp)) {
                ProductSummarizedPreviewCard(
                    productData,
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
}