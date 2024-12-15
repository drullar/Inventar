package io.drullar.inventar.ui.components.screen.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.ProductPreviewCard

@Composable
fun ProductsLazyGrid(
    products: MutableList<ProductDTO>,
    onProductSelectCallback: (ProductDTO?) -> Unit,
    selectionIsAllowed: Boolean
) {
    val selectedProduct = mutableStateOf<ProductDTO?>(null)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        itemsIndexed(products) { index, productData ->
            Box(modifier = Modifier.padding(5.dp)) {
                ProductPreviewCard(
                    productData,
                    onClickCallback = onProductSelectCallback,
                    isSelected = productData == selectedProduct.value,
                    selectionIsAllowed = selectionIsAllowed
                )
            }

        }
    }
}