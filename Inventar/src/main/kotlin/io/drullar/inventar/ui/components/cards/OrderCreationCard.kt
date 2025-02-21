package io.drullar.inventar.ui.components.cards

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.utils.Icons
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun OrderCreationCard(
    order: OrderDTO,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    onProductRemove: (ProductDTO) -> Unit
) {
    var selectedRowIndex by remember { mutableStateOf<Int?>(null) }
    val productsMap = order.productToQuantity

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(1.dp, Color.Black, roundedBorderShape()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth(0.4f)) {
                    Text(
                        text = "Created on:",
                        textAlign = TextAlign.End,
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )
                    Text(
                        text = order.creationDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )
                }
                Text(
                    "Order #${order.orderId}",
                    textDecoration = TextDecoration.Underline,
                    fontSize = TextUnit(24f, TextUnitType.Sp)
                )
            }

            Spacer(Modifier.padding(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(
                    items = productsMap.keys.toList(),
                    key = { _, product -> product.uid }) { rowIndex, product ->
                    OrderCreationRow(
                        productDTO = product,
                        quantity = productsMap[product]!!,
                        onSelectCallback = { selectedRowIndex = rowIndex },
                        onQuantityChangeCallback = { newQuantity ->
                            productsMap[product] = newQuantity
                        },
                        onRemoveCallback = { onProductRemove(it) },
                    )
                }
            }
        }

        GroupedButtons(
            Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
            onCancel,
            onComplete
        )
    }
}

@Composable
private fun GroupedButtons(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onComplete: () -> Unit,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TerminalOrderButton(
            "Cancel",
            Color.White,
            onCancel
        )
        TerminalOrderButton(
            "Complete",
            Color.White,
            onComplete
        )
    }
}

@Composable
private fun TerminalOrderButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors().copy(containerColor = backgroundColor),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun OrderCreationRow(
    productDTO: ProductDTO,
    quantity: Int,
    onSelectCallback: () -> Unit,
    onQuantityChangeCallback: (Int) -> Unit,
    onRemoveCallback: (ProductDTO) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(1.dp, Color.Black)
            .clickable(onClick = onSelectCallback)
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            BasicTextField(
                value = "$quantity",
                onValueChange = { onQuantityChangeCallback(quantity) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .height(15.dp)
                    .widthIn(15.dp, 50.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                "x",
                Modifier.align(Alignment.CenterVertically).height(15.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 5.dp).align(Alignment.CenterVertically))
            Text(
                productDTO.name,
                modifier = Modifier.fillMaxWidth(0.8f)
                    .align(Alignment.CenterVertically)
                    .height(15.dp),
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            IconButton(onClick = {
                onRemoveCallback(productDTO)
                println("Button clicked")
            }) {
                Icon(
                    painterResource(Icons.CROSS_RED),
                    "remove from order",
                    Modifier.size(30.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun OrderCreationCardPreview() {
    OrderCreationCard(
        OrderDTO(
            orderId = 1,
            productToQuantity = mutableMapOf(
                ProductDTO(1, "Name") to 2,
                ProductDTO(1, "ASDFS") to 2,
                ProductDTO(1, "Adq") to 2
            ),
            creationDate = LocalDateTime.now(),
            status = OrderStatus.DRAFT
        ),
        {},
        {},
        {}
    )
}