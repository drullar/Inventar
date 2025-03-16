package io.drullar.inventar.ui.components.cards

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.viewmodel.delegates.getText
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale

@Composable
fun OrdersListPreviewCard(
    orders: List<OrderDTO>,
    activeLocale: Locale,
    onOrderCompletion: (OrderDTO) -> Unit,
    onOrderSelect: (OrderDTO) -> Unit,
    onOrderTermination: (OrderDTO) -> Unit
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
                SimpleOrderRow(
                    order,
                    activeLocale,
                    onOrderCompletion,
                    onOrderSelect,
                    onOrderTermination
                )
            }
        }
    }
}

@Composable
fun SimpleOrderRow(
    orderDTO: OrderDTO,
    activeLocale: Locale,
    onComplete: (OrderDTO) -> Unit,
    onSelect: (OrderDTO) -> Unit,
    onTerminate: (OrderDTO) -> Unit,
    showOrderStatus: Boolean = false
) {
    val order by remember { mutableStateOf(orderDTO) }

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
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontWeight = FontWeight.Black
            )
            Text(
                month,
                textAlign = TextAlign.Center,
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontStyle = FontStyle.Italic
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.2f).align(Alignment.CenterVertically)
        ) {
            Text(
                "${getText("label.order")} #${orderDTO.orderId}",
                fontSize = TextUnit(20f, TextUnitType.Sp),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            if (showOrderStatus) {
                Text(
                    text = orderDTO.status.text.value.uppercase(),
                    color = when (orderDTO.status) {
                        OrderStatus.COMPLETED -> Colors.DarkGreen
                        else -> Color.Gray
                    },
                    fontSize = TextUnit(20f, TextUnitType.Sp),
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 5.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically).fillMaxWidth(0.5f)
                .padding(start = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                orderDTO.getTotalPrice().toString()
                        + Currency.getInstance("BGN"), // TODO use java currency and Singleton for currency
                fontSize = TextUnit(20f, TextUnitType.Sp),
                modifier = Modifier.fillMaxWidth(0.2f)
            )
            if (orderDTO.status != OrderStatus.COMPLETED) {
                TextButton(getText("label.complete"), onClick = { onComplete(orderDTO) })
                TextButton(getText("label.terminate"), onClick = { onTerminate(orderDTO) })
            }
        }

    }
}