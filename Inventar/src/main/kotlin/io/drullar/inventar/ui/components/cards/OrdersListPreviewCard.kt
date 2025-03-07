package io.drullar.inventar.ui.components.cards

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.Colors
import java.time.LocalDateTime
import java.util.Currency

@Composable
fun OrdersListPreviewCard(
    orders: List<OrderDTO>,
    onOrderCompletion: (OrderDTO) -> Unit,
    onOrderSelect: (OrderDTO) -> Unit,
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
                SimpleOrderRow(order, onOrderCompletion, onOrderSelect)
            }
        }
    }
}

@Composable
fun SimpleOrderRow(
    orderDTO: OrderDTO,
    onComplete: (OrderDTO) -> Unit,
    onSelect: (OrderDTO) -> Unit,
    showOrderStatus: Boolean = false
) {
    Row(
        Modifier
            .border(0.5.dp, Color.Black)
            .wrapContentHeight()
            .clickable(onClick = { onSelect(orderDTO) })
            .fillMaxWidth()
    ) {
        val day = orderDTO.creationDate.dayOfMonth
        val month = orderDTO.creationDate.month
        Column(Modifier.fillMaxWidth(0.2f).padding(horizontal = 5.dp, vertical = 2.dp)) {
            Text(
                day.toString(),
                textAlign = TextAlign.Center,
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontWeight = FontWeight.Black
            )
            Text(
                month.name,
                textAlign = TextAlign.Center,
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontStyle = FontStyle.Italic
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(0.2f).align(Alignment.CenterVertically)
        ) {
            Text(
                "Order #${orderDTO.orderId}",
                fontSize = TextUnit(20f, TextUnitType.Sp),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            if (showOrderStatus) {
                Text(
                    text = orderDTO.status.toString().uppercase(),
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
                Button(onClick = { onComplete(orderDTO) }) {
                    Text("Complete", maxLines = 1)
                }
                Button(onClick = {}) {
                    Text("Terminate", maxLines = 1)
                }
            }
        }

    }
}

@Composable
@Preview
private fun DraftOrderRowPreview() {
    SimpleOrderRow(
        OrderDTO(
            1001,
            mutableMapOf(ProductDTO(1, "My Product", sellingPrice = 1.23) to 1),
            LocalDateTime.now(),
            OrderStatus.DRAFT
        ),
        {},
        {},
        true
    )
}

@Composable
@Preview
private fun OrdersListPreviewCardPreview() {
    OrdersListPreviewCard(
        listOf(
            OrderDTO(
                1,
                mutableMapOf(),
                LocalDateTime.now(),
                OrderStatus.DRAFT
            ),
            OrderDTO(
                2,
                mutableMapOf(ProductDTO(2, "My PRoduct", sellingPrice = 1.234) to 1),
                LocalDateTime.now().minusWeeks(3),
                OrderStatus.DRAFT
            )
        ),
        {},
        {}
    )
}