package io.drullar.inventar.ui.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.ui.components.button.IconButton
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.LayoutStyle
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.utils.Icons
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale

@Composable
fun OrdersListPreviewCard(
    orders: List<OrderDTO>,
    style: LayoutStyle,
    activeLocale: Locale,
    onOrderCompletion: (OrderDTO) -> Unit,
    onOrderSelect: (OrderDTO) -> Unit,
    onOrderTermination: (OrderDTO) -> Unit,
    currency: Currency
) {
    val scrollableState = rememberScrollState()
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(),
        modifier = Modifier
            .fillMaxWidth()
            .scrollable(state = scrollableState, orientation = Orientation.Vertical)
    )
    {
        LazyColumn {
            items(
                items = orders.sortedByDescending { it.creationDate },
                key = { it.orderId }) { order ->
                if (style == LayoutStyle.NORMAL)
                    NormalOrderPreviewRow(
                        order,
                        activeLocale,
                        onOrderCompletion,
                        onOrderSelect,
                        onOrderTermination,
                        false,
                        currency
                    )
                else
                    CompactOrderPreviewRow(
                        order,
                        onOrderCompletion,
                        onOrderTermination,
                        onOrderSelect,
                        false,
                        currency
                    )
            }
        }
    }
}

@Composable
fun NormalOrderPreviewRow(
    orderDTO: OrderDTO,
    activeLocale: Locale,
    onComplete: (OrderDTO) -> Unit,
    onSelect: (OrderDTO) -> Unit,
    onTerminate: (OrderDTO) -> Unit,
    showOrderStatus: Boolean,
    currency: Currency
) {
    Row(
        Modifier
            .border(0.5.dp, Color.Black)
            .wrapContentHeight()
            .clickable(onClick = { onSelect(orderDTO) })
            .fillMaxWidth()
    ) {
        val day = orderDTO.creationDate.dayOfMonth
        val month =
            orderDTO.creationDate.month.getDisplayName(TextStyle.FULL, activeLocale)
        Column(Modifier.fillMaxWidth(0.2f).padding(horizontal = 5.dp, vertical = 2.dp)) {
            Text(
                day.toString(),
                textAlign = TextAlign.Center,
                style = appTypography().bodyLarge
            )
            Text(
                month,
                textAlign = TextAlign.Center,
                style = appTypography().bodyLarge,
                fontStyle = FontStyle.Italic
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.2f).align(Alignment.CenterVertically)
        ) {
            Text(
                "${getText("label.order")} #${orderDTO.orderId}",
                style = appTypography().bodyLarge,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            if (showOrderStatus) {
                Text(
                    text = orderDTO.status.text.value.uppercase(),
                    color = orderDTO.status.associatedColor.value,
                    style = appTypography().bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 5.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)
                .fillMaxWidth(0.3f)
                .padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                orderDTO.getTotalPrice().toString() + currency.symbol,
                style = appTypography().bodyLarge,
                modifier = Modifier.fillMaxWidth(0.2f)
            )
            if (orderDTO.status == OrderStatus.DRAFT) {
                TextButton(
                    getText("label.complete"),
                    onClick = { onComplete(orderDTO) },
                    backgroundColor = Colors.DarkGreen,
                    borderColor = Colors.DarkGreen
                )
                TextButton(
                    getText("label.terminate"),
                    onClick = { onTerminate(orderDTO) },
                    backgroundColor = Color.Red,
                    borderColor = Color.Red
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompactOrderPreviewRow(
    orderDTO: OrderDTO,
    onComplete: (OrderDTO) -> Unit,
    onTerminate: (OrderDTO) -> Unit,
    onSelect: (OrderDTO) -> Unit,
    showOrderStatus: Boolean,
    currency: Currency
) {
    Column(
        Modifier.onClick { onSelect(orderDTO) }
            .fillMaxWidth()
            .border(0.5.dp, Color.Black)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            val (day, month) = orderDTO.creationDate.let {
                it.dayOfMonth to it.month.getDisplayName(
                    TextStyle.FULL_STANDALONE,
                    Locale.ENGLISH
                )
            }
            Row {
                Text(
                    text = day.toString(),
                    style = appTypography().bodyLarge
                )
                Text(
                    text = month,
                    style = appTypography().bodyLarge,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }

            Text(
                "${getText("label.order")}# ${orderDTO.orderId}",
                style = appTypography().bodyMedium
            )
            if (showOrderStatus) {
                Text(
                    text = orderDTO.status.text.value.uppercase(),
                    style = appTypography().bodyLarge,
                    color = orderDTO.status.associatedColor.value
                )
            }

        }

        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "${getText("field.total")}: ",
                style = appTypography().bodyLarge,
            )
            Text(
                text = orderDTO.getTotalPrice().toString() + currency,
                style = appTypography().bodyLarge,
            ) //TODO currency
        }

        if (orderDTO.status == OrderStatus.DRAFT) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val iconSize = 30.dp
                IconButton(onClick = {
                    onComplete(orderDTO)
                }, onHoverText = getText("label.complete")) {
                    Image(
                        painterResource(Icons.POSITIVE_SIGN),
                        getText("label.terminate"),
                        modifier = Modifier.size(iconSize)
                    )
                }

                IconButton(onClick = {
                    onTerminate(orderDTO)
                }, onHoverText = getText("label.terminate")) {
                    Image(
                        painterResource(Icons.NEGATIVE_SIGN),
                        getText("label.terminate"),
                        Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}