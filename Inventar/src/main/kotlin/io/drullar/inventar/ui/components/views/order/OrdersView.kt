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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.drullar.inventar.SortingOrder
import io.drullar.inventar.persistence.repositories.OrderRepository
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.cards.SimpleOrderRow
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.viewmodel.delegates.getText

const val PAGE_SIZE = 20

@Composable
fun OrdersView(viewModel: OrderViewViewModel) {
    val sortingOrder by viewModel._sortingOrder.collectAsState()
    val sortingBy by viewModel._sortBy.collectAsState()
    val settings by viewModel.getSettings().collectAsState()
    var isOrderByDropdownExtended by remember { mutableStateOf(false) }
    var isSortingOrderDropDownExtended by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    var _page by remember { mutableStateOf(1) }
    var _orders = remember {
        mutableStateListOf<OrderDTO>().apply {
            addAll(
                viewModel.fetchOrders(
                    page = _page,
                    pageSize = PAGE_SIZE,
                    sortBy = sortingBy,
                    order = sortingOrder
                ).getOrNull()?.items ?: emptyList()
            )
        }
    }

    LaunchedEffect(sortingOrder, sortingBy) {
        _page = 1
        _orders.clear()
        _orders.addAll(
            viewModel.fetchOrders(
                page = _page,
                pageSize = PAGE_SIZE,
                sortBy = sortingBy,
                order = sortingOrder
            ).getOrNull()?.items ?: emptyList()
        )
    }

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                text = getText("order.new"),
                onClick = { viewModel.newOrder() },
                backgroundColor = Colors.DarkGreen,
                borderColor = Colors.DarkGreen
            )

            Row(
                modifier = Modifier.wrapContentHeight().wrapContentWidth()
                    .padding(bottom = 10.dp)
            ) {
                TextButton(
                    text = "${getText("label.sort")}: ${getText(sortingBy.text)}",
                    onClick = { isOrderByDropdownExtended = !isOrderByDropdownExtended }) {
                    DropdownMenu(
                        expanded = isOrderByDropdownExtended,
                        onDismissRequest = { isOrderByDropdownExtended = false }
                    ) {
                        DropdownMenuItem({ Text(getText("field.date")) }, {
                            viewModel.sortOrdersBy(OrderRepository.SortBy.CREATION_DATE)
                            isOrderByDropdownExtended = false
                        })
                        DropdownMenuItem({ Text(getText("field.number")) }, {
                            isOrderByDropdownExtended = false
                            viewModel.sortOrdersBy(OrderRepository.SortBy.NUMBER)
                        })
//                        DropdownMenuItem({ Text(getText("field.total.price")) }, {
//                            isOrderByDropdownExtended = false
//                            viewModel.sortOrdersBy(OrderViewViewModel.OrderBy.TOTAL_PRICE)
//                        })
                        DropdownMenuItem({ Text(getText("field.status")) }, {
                            viewModel.sortOrdersBy(OrderRepository.SortBy.STATUS)
                            isOrderByDropdownExtended = false
                        })
                    }
                }

                TextButton(
                    text = "${getText("label.sorting.order")}:  ${getText(sortingOrder.text)}",
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
                            { Text(getText(SortingOrder.ASCENDING.text)) },
                            {
                                isSortingOrderDropDownExtended = false
                                viewModel.setSortingOrder(SortingOrder.ASCENDING)
                            }
                        )
                        DropdownMenuItem(
                            { Text(getText(SortingOrder.DESCENDING.text)) },
                            {
                                isSortingOrderDropDownExtended = false
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
                    items = _orders,
                    key = { _, item -> item.orderId.toString().plus(item.status) }) { index, item ->
                    if (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == _orders.size - 1) {
                        _page += 1
                        _orders += viewModel.fetchOrders(
                            _page,
                            PAGE_SIZE,
                            sortingBy,
                            sortingOrder
                        ).getOrNull()?.items ?: emptyList()
                    }
                    SimpleOrderRow(
                        orderDTO = item,
                        activeLocale = settings.language.locale,
                        onComplete = {
                            val itemIndex = _orders.indexOf(item)
                            val updateItem =
                                viewModel.changeOrderStatus(item, OrderStatus.COMPLETED)

                            if (updateItem.isSuccess)
                                updateItem(_orders, itemIndex, updateItem.getOrNull()!!)
                        },
                        onSelect = { order ->
                            viewModel.setPreview(OrderDetailsPreview(order))
                            viewModel.setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
                        },
                        onTerminate = {
                            val itemIndex = _orders.indexOf(item)
                            val updateItem =
                                viewModel.changeOrderStatus(item, OrderStatus.TERMINATED)

                            if (updateItem.isSuccess)
                                updateItem(_orders, itemIndex, updateItem.getOrNull()!!)
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

fun updateItem(orders: MutableList<OrderDTO>, itemIndex: Int, updateWith: OrderDTO) {
    orders[itemIndex] = orders[itemIndex].copy(status = updateWith.status)
}