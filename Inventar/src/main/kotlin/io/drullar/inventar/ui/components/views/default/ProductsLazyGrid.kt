package io.drullar.inventar.ui.components.views.default

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.ProductSummarizedPreviewCard
import io.drullar.inventar.ui.components.views.order.PAGE_SIZE
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Currency

const val PRODUCTS_PER_PAGE = 40 //TODO increase


@Composable
fun ProductsLazyGrid(
    viewModel: DefaultViewViewModel,
    currency: Currency,
    onProductSelectCallback: (ProductDTO) -> Unit,
    selectionIsAllowed: Boolean,
    onProductDeleteRequest: (ProductDTO) -> Unit,
    onProductEditRequest: (ProductDTO) -> Unit,
    onAddProductToOrderRequest: (ProductDTO) -> Unit
) {
    val selectedProduct by remember { mutableStateOf<ProductDTO?>(null) }
    var page by remember { mutableStateOf(1) }
    val sortBy by viewModel._sortBy.collectAsState()
    val sortingOrder by viewModel._sortingOrder.collectAsState()
    val products = remember {
        mutableStateListOf<ProductDTO>().apply {
            addAll(
                viewModel.fetchProducts(
                    page, PRODUCTS_PER_PAGE, sortBy, sortingOrder
                ).items
            )
        }
    }

//    LaunchedEffect(sortingOrder, sortBy) {
//        page = 1
//        products.clear()
//        products.addAll(
//            viewModel.fetchProducts(
//                page = page,
//                pageSize = PRODUCTS_PER_PAGE,
//                sortBy = sortBy,
//                order = sortingOrder
//            ).items
//        )
//    }

    val scrollState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(10.dp),
        state = scrollState
    ) {
        itemsIndexed(
            items = products,
            key = { _, product -> product.uid }
        ) { index, product ->

            if (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == products.size - 1) {
                page += 1
                products.addAll(
                    viewModel.fetchProducts(
                        page,
                        PRODUCTS_PER_PAGE,
                        sortBy,
                        sortingOrder
                    ).items
                )
            }

            ProductSummarizedPreviewCard(
                product,
                currency = currency,
                onClickCallback = onProductSelectCallback,
                isSelected = product == selectedProduct,
                selectionIsAllowed = selectionIsAllowed,
                onEditRequest = onProductEditRequest,
                onDeleteRequest = onProductDeleteRequest,
                onAddToOrderRequest = onAddProductToOrderRequest
            )
        }
    }
}