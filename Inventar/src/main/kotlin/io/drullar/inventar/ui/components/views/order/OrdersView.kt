package io.drullar.inventar.ui.components.views.order

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.viewmodel.delegates.getText

@Composable
fun OrdersView(viewModel: OrderViewViewModel) {
    val orders = viewModel._orders.collectAsState()
    val sortingOrder by viewModel._sortingOrder.collectAsState()
    val sortingBy by viewModel._orderBy.collectAsState()
    val settings by viewModel.getSettings().collectAsState()
    var isOrderByDropdownExtended by remember { mutableStateOf(false) }
    var isSortingOrderDropDownExtended by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            TextButton(
                text = getText("order.new"),
                onClick = { viewModel.newOrder() },
                backgroundColor = Colors.DarkGreen,
                borderColor = Colors.DarkGreen
            )

            Row(
                modifier = Modifier.wrapContentHeight().wrapContentWidth().padding(bottom = 10.dp)
            ) {
                TextButton(
                    text = "${getText("label.sort")}: ${sortingBy.text}",
                    onClick = { isOrderByDropdownExtended = !isOrderByDropdownExtended }) {
                    DropdownMenu(
                        expanded = isOrderByDropdownExtended,
                        onDismissRequest = { isOrderByDropdownExtended = false }
                    ) {
                        DropdownMenuItem({ Text(getText("field.date")) }, {
                            viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.DATE)
                            isOrderByDropdownExtended = false
                        })
                        DropdownMenuItem({ Text(getText("field.number")) }, {
                            isOrderByDropdownExtended = false
                            viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.ID)
                        })
                        DropdownMenuItem({ Text(getText("field.total.price")) }, {
                            isOrderByDropdownExtended = false
                            viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.TOTAL_PRICE)
                        })
                        DropdownMenuItem({ Text(getText("field.status")) }, {
                            viewModel.orderOrdersBy(OrderViewViewModel.OrderBy.STATUS)
                            isOrderByDropdownExtended = false
                        })
                    }
                }

                TextButton(
                    text = "${getText("label.sorting.order")}:  ${sortingOrder.text}",
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
                            { Text(SortingOrder.ASCENDING.text) },
                            {
                                isOrderByDropdownExtended = false
                                viewModel.setSortingOrder(SortingOrder.ASCENDING)
                            }
                        )
                        DropdownMenuItem(
                            { Text(SortingOrder.DESCENDING.text) },
                            {
                                isOrderByDropdownExtended = false
                                viewModel.setSortingOrder(SortingOrder.DESCENDING)
                            }
                        )
                    }
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
                    SimpleOrderRow(
                        orderDTO = item,
                        activeLocale = settings.language.locale,
                        onComplete = {
                            //TODO implement
                        },
                        onSelect = { order ->
                            viewModel.setPreview(OrderDetailsPreview(order))
                            viewModel.setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
                        },
                        onTerminate = {
                            //TODO implement
                        },
                        showOrderStatus = true
                    )
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