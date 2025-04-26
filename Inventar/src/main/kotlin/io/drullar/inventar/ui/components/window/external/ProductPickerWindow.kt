package io.drullar.inventar.ui.components.window.external

import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import io.drullar.inventar.persistence.repositories.impl.ProductsRepository
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.ui.components.search.SearchBar
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import kotlinx.coroutines.flow.drop

private const val ITEMS_PER_PAGE = 10

@Composable
fun ProductPickerDialog(
    onDismissRequest: () -> Unit,
    analyticsViewModel: AnalyticsViewModel, //TODO extract product search method in a separate abstraction and pass that instead of the entire viewModel
    onProductSelect: (ProductDTO) -> Unit,
) {
    val windowState = remember {
        WindowState(width = 500.dp, height = 500.dp, placement = WindowPlacement.Floating)
    }

    Window(
        onCloseRequest = onDismissRequest,
        state = windowState,
        alwaysOnTop = true,
        icon = painterResource(Icons.APP_ICON)
    ) {
        val scrollState = rememberLazyListState()
        var searchQuery by remember { mutableStateOf("") }
        var page by remember { mutableStateOf(1) }
        val products = remember {
            mutableListOf<ProductDTO>().also {
                it.addAll(
                    analyticsViewModel.searchForProduct(
                        searchQuery,
                        pagedRequest = PagedRequest(
                            page,
                            ITEMS_PER_PAGE,
                            sortBy = ProductsRepository.SortBy.NAME,
                            order = SortingOrder.ASCENDING
                        )
                    ).items
                )
            }
        }

        LaunchedEffect(searchQuery) {
            snapshotFlow { searchQuery }
                .drop(1)
                .collect {
                    page = 1
                    products.clear()
                    products.addAll(
                        analyticsViewModel.searchForProduct(
                            searchQuery,
                            pagedRequest = PagedRequest(
                                page,
                                ITEMS_PER_PAGE,
                                sortBy = ProductsRepository.SortBy.NAME,
                                order = SortingOrder.ASCENDING
                            )
                        ).items //TODO move all occurrences of searchForProduct call in a function `fetchNextPage` or something similar
                    )
                }
        }
        
        OutlinedCard(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            SearchBar {
                searchQuery = it
            }
            Box {
                LazyColumn(state = scrollState, modifier = Modifier.align(Alignment.TopStart)) {
                    items(items = products, key = { it.hashCode() }) {
                        if (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == products.size - 1) {
                            page += 1
                            val newBatch = analyticsViewModel.searchForProduct(
                                searchQuery,
                                pagedRequest = PagedRequest(
                                    page,
                                    ITEMS_PER_PAGE,
                                    sortBy = ProductsRepository.SortBy.NAME,
                                    order = SortingOrder.ASCENDING
                                )
                            ).items

                            products.addAll(newBatch)
                        }
                        Card(
                            onClick = { onProductSelect(it) },
                            modifier = Modifier.fillMaxWidth().padding(5.dp).wrapContentHeight(),
                            colors = CardDefaults.cardColors().copy(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(5.dp)) {
                                Text(
                                    "${getText("field.number")}: ${it.uid}",
                                    style = appTypography().bodyMedium
                                )
                                Text(
                                    "${getText("field.name")}: ${it.name}",
                                    style = appTypography().bodyMedium
                                )
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    ScrollbarAdapter(scrollState),
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}