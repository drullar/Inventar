package io.drullar.inventar.ui.components.views.order

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.drullar.inventar.SortingOrder
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.cards.SimpleOrderRow
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.data.OrderCreationPreview
import io.drullar.inventar.ui.style.Colors

@Composable
fun OrdersView(viewModel: OrderViewViewModel) {
    val orders = viewModel._orders.collectAsState()
    val sortingOrder by viewModel._sortingOrder.collectAsState()
    val sortingBy by viewModel._orderBy.collectAsState()
    var isOrderByDropdownExtended by remember { mutableStateOf(false) }
    var isSortingOrderDropDownExtended by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Row(modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(bottom = 10.dp)) {
            TextButton(
                text = "Sort by: ${sortingBy.asString}",
                onClick = { isOrderByDropdownExtended = !isOrderByDropdownExtended }) {
                DropdownMenu(
                    expanded = isOrderByDropdownExtended,
                    onDismissRequest = { isOrderByDropdownExtended = false }
                ) {
                    DropdownMenuItem({ Text("Date") }, {
                        viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.DATE)
                        isOrderByDropdownExtended = false
                    })
                    DropdownMenuItem({ Text("Order") }, {
                        isOrderByDropdownExtended = false
                        viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.ID)
                    })
                    DropdownMenuItem({ Text("Price") }, {
                        isOrderByDropdownExtended = false
                        viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.TOTAL_PRICE)
                    })
                    DropdownMenuItem({ Text("Status") }, {
                        viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.STATUS)
                        isOrderByDropdownExtended = false
                    })
                }
            }

            TextButton(
                text = "Order: ".plus(
                    if (sortingOrder == SortingOrder.ASCENDING) {
                        "Ascending"
                    } else {
                        "Descending"
                    }
                ),
                onClick = {
                    isSortingOrderDropDownExtended = !isSortingOrderDropDownExtended
                },
                modifier = Modifier.padding(start = 10.dp),
                backgroundColor = Color.White,
                textColor = Colors.BrightBlue,
                borderColor = Colors.BrightBlue
            ) {
                DropdownMenu(
                    expanded = isSortingOrderDropDownExtended,
                    onDismissRequest = { isSortingOrderDropDownExtended = false }
                ) {
                    DropdownMenuItem(
                        { Text("Ascending") },
                        {
                            isOrderByDropdownExtended = false
                            viewModel.setSortingOrder(SortingOrder.ASCENDING)
                        }
                    )
                    DropdownMenuItem(
                        { Text("Descending") },
                        {
                            isOrderByDropdownExtended = false
                            viewModel.setSortingOrder(SortingOrder.DESCENDING)
                        }
                    )
                }
            }
        }
        Box {
            LazyColumn(state = scrollState, modifier = Modifier.padding(end = 12.dp)) {
                itemsIndexed(
                    items = orders.value,
                    key = { _, item -> item.orderId }) { index, item ->
                    if (index == orders.value.size - 1) {
                        viewModel.loadNextOrdersPage()
                    }
                    SimpleOrderRow(item, {}, { order ->
                        viewModel.setPreview(OrderCreationPreview(order))
                        viewModel.setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
                    }, true)
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                )
            )
        }
    }
}