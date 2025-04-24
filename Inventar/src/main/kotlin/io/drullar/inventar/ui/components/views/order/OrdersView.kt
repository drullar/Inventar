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
import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.cards.CompactOrderRow
import io.drullar.inventar.ui.components.cards.NormalOrderRow
import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.components.window.dialog.AlertDialog
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.data.OrderDetailsPreview
import io.drullar.inventar.ui.data.OrderWindowPayload
import io.drullar.inventar.ui.provider.getLayoutStyle
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.LayoutStyle

const val PAGE_SIZE = 20

@Composable
fun OrdersView(viewModel: OrderViewViewModel) {
    val activeDialog = viewModel.getActiveWindow().collectAsState()

    val sortingOrder by viewModel._sortingOrder.collectAsState()
    val sortingBy by viewModel._sortBy.collectAsState()
    val settings by viewModel.getSettings().collectAsState()
    var isOrderByDropdownExtended by remember { mutableStateOf(false) }
    var isSortingOrderDropDownExtended by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    var page by remember { mutableStateOf(1) }
    val orders = remember {
        mutableStateListOf<OrderDTO>().apply {
            addAll(
                viewModel.fetchOrders(
                    PagedRequest(
                        page = page,
                        pageSize = PAGE_SIZE,
                        sortBy = sortingBy,
                        order = sortingOrder
                    )
                ).items
            )
        }
    }

    LaunchedEffect(sortingOrder, sortingBy) {
        page = 1
        orders.clear()
        orders.addAll(
            viewModel.fetchOrders(
                PagedRequest(
                    page = page,
                    pageSize = PAGE_SIZE,
                    sortBy = sortingBy,
                    order = sortingOrder
                )
            ).items
        )
    }

    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                text = getText("order.new"),
                onClick = { viewModel.createOrder(emptyMap()) },
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
                            viewModel.sortOrdersBy(OrderRepository.OrderSortBy.CREATION_DATE)
                            isOrderByDropdownExtended = false
                        })
                        DropdownMenuItem({ Text(getText("field.number")) }, {
                            isOrderByDropdownExtended = false
                            viewModel.sortOrdersBy(OrderRepository.OrderSortBy.NUMBER)
                        })
//                        DropdownMenuItem({ Text(getText("field.total.price")) }, {
//                            isOrderByDropdownExtended = false
//                            viewModel.sortOrdersBy(OrderViewViewModel.OrderBy.TOTAL_PRICE)
//                        }) TODO implement
                        DropdownMenuItem({ Text(getText("field.status")) }, {
                            viewModel.sortOrdersBy(OrderRepository.OrderSortBy.STATUS)
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
                    items = orders,
                    key = { _, item -> item.orderId.toString().plus(item.status) }) { index, item ->
                    if (scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == orders.size - 1) {
                        page += 1
                        orders += viewModel.fetchOrders(
                            PagedRequest(
                                page,
                                PAGE_SIZE,
                                sortingOrder,
                                sortingBy
                            )
                        ).items
                    }

                    if (getLayoutStyle() == LayoutStyle.NORMAL)
                        NormalOrderRow(
                            order = item,
                            activeLocale = settings.language.locale,
                            onComplete = { hasProblems, order ->
                                if (!hasProblems) {
                                    val itemIndex = orders.indexOf(item)
                                    val updateItem = viewModel.completeOrder(item)
                                    updateItem(orders, itemIndex, updateItem)
                                } else {
                                    viewModel.setActiveWindow(
                                        DialogWindowType.ORDER_QUANTITY_ISSUES_ALERT,
                                        OrderWindowPayload(order)
                                    )
                                }
                            },
                            onSelect = { order ->
                                viewModel.setPreview(OrderDetailsPreview(order))
                                viewModel.setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
                            },
                            onTerminate = {
                                val itemIndex = orders.indexOf(item)
                                val updateItem =
                                    viewModel.terminateOrder(item)
                                updateItem(orders, itemIndex, updateItem)
                            },
                            showOrderStatus = true,
                            currency = settings.defaultCurrency,
                            validateProductAvailability = {
                                viewModel.validateProductsAvailability(
                                    it
                                )
                            }
                        )
                    else CompactOrderRow(
                        order = item,
                        onComplete = { hasProblems, order ->
                            if (!hasProblems) {
                                val itemIndex = orders.indexOf(item)
                                val updateItem =
                                    viewModel.completeOrder(item)
                                updateItem(orders, itemIndex, updateItem)
                            }
                        },
                        onSelect = { order ->
                            viewModel.setPreview(OrderDetailsPreview(order))
                            viewModel.setNavigationDestination(NavigationDestination.PRODUCTS_PAGE)
                        },
                        onTerminate = {
                            val itemIndex = orders.indexOf(item)
                            val updateItem =
                                viewModel.terminateOrder(item)
                            updateItem(orders, itemIndex, updateItem)
                        },
                        showOrderStatus = true,
                        currency = settings.defaultCurrency,
                        validateProductAvailability = { viewModel.validateProductsAvailability(it) }
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

    when (activeDialog.value) {
        DialogWindowType.ORDER_QUANTITY_ISSUES_ALERT -> {
            val order = viewModel.getWindowPayload<OrderDTO>().value.getData()
            AlertDialog(
                text = getText("warning.order.quantity"),
                resolveButtonText = getText("label.continue.anyway"),
                cancelButtonText = getText("label.cancel"),
                onResolve = {
                    val itemIndex = orders.indexOf(order)
                    val updatedOrder = viewModel.completeOrder(order)
                    updateItem(orders, itemIndex, updatedOrder)
                    viewModel.setActiveWindow(null, EmptyPayload())
                },
                onCancel = { viewModel.setActiveWindow(null, EmptyPayload()) }
            )
        }

        else -> Unit
    }
}

fun updateItem(orders: MutableList<OrderDTO>, itemIndex: Int, updateWith: OrderDTO) {
    orders[itemIndex] = orders[itemIndex].copy(status = updateWith.status)
}