package io.drullar.inventar.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.provider.getText
import java.time.format.DateTimeFormatter

@Composable
fun OrderDetailPreviewCard(
    order: OrderDTO,
    onTerminate: () -> Unit,
    onComplete: () -> Unit,
    onProductValueChange: (ProductDTO, Int) -> Unit,
    onProductRemove: (ProductDTO) -> Unit
) {
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
                        text = getText("label.created.on"),
                        textAlign = TextAlign.End,
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )
                    Text(
                        text = order.creationDate.format(
                            DateTimeFormatter.ofPattern(
                                "dd.MM.yyyy HH:mm"
                            )
                        ),
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )
                }
                Text(
                    getText("label.order") + "#${order.orderId}",
                    textDecoration = TextDecoration.Underline,
                    fontSize = TextUnit(24f, TextUnitType.Sp)
                )

            }

            Spacer(Modifier.padding(10.dp))

            Box {

                val scrollState = rememberLazyListState()
                LazyColumn(Modifier.fillMaxHeight(0.6f).padding(end = 12.dp), scrollState) {
                    items(
                        items = productsMap.keys.toList(),
                        key = { it.uid }
                    ) { product ->
                        OrderCreationRow(
                            productDTO = product,
                            isModifiable = order.status == OrderStatus.DRAFT,
                            quantity = productsMap[product]!!,
                            onSelectCallback = { /*TODO*/ },
                            onQuantityChangeCallback = { newQuantity ->
                                onProductValueChange(product, newQuantity)
                            },
                            onRemoveCallback = { onProductRemove(it) },
                        )
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd)
                        .fillMaxHeight(0.6f),
                    adapter = rememberScrollbarAdapter(
                        scrollState = scrollState
                    ),
                    style = LocalScrollbarStyle.current.copy(
                        thickness = 10.dp,
                        unhoverColor = Colors.PlatinumGray, hoverColor = Color.DarkGray
                    )
                )
            }

        }

        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Text(
                text = "${getText("field.total.price")}: ${order.getTotalPrice()} BGN", //TODO currency
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W100,
                fontSize = TextUnit(30f, TextUnitType.Sp)
            ) // TODO use currency
            Spacer(Modifier.padding(vertical = 10.dp))
            if (order.status == OrderStatus.DRAFT)
                GroupedButtons(
                    Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                    onTerminate,
                    onComplete
                )
        }

    }
}

@Composable
private fun GroupedButtons(
    modifier: Modifier = Modifier,
    onTerminate: () -> Unit,
    onComplete: () -> Unit,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceAround) {
        TextButton(getText("label.complete"), onComplete)

        TextButton(
            getText("label.terminate"),
            onTerminate,
            backgroundColor = Color.White,
            textColor = Color.Red,
            borderColor = Color.Red
        )
    }
}

@Composable
private fun OrderCreationRow(
    productDTO: ProductDTO,
    isModifiable: Boolean,
    quantity: Int,
    onSelectCallback: () -> Unit,
    onQuantityChangeCallback: (Int) -> Unit,
    onRemoveCallback: (ProductDTO) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(50.dp, 75.dp)
            .border(1.dp, Color.Black)
            .clickable(onClick = onSelectCallback)
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            BasicTextField(
                value = if (quantity > 0) quantity.toString() else "",
                onValueChange = { value ->
                    if (isModifiable) {
                        if (value.isEmpty()) onQuantityChangeCallback(0)
                        else value.toIntOrNull()?.let {
                            onQuantityChangeCallback(it)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = TextStyle.Default.copy(textAlign = TextAlign.Center),
                modifier = Modifier
                    .background(color = Colors.PlatinumGray)
                    .border(BorderStroke(0.1.dp, Color.Black), RoundedCornerShape(3.dp))
                    .widthIn(15.dp, 25.dp)
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            )
            Text(
                "x",
                Modifier.align(Alignment.CenterVertically).height(15.dp).padding(start = 2.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 5.dp).align(Alignment.CenterVertically))
            Text(
                productDTO.name,
                modifier = Modifier.fillMaxWidth(0.4f)
                    .align(Alignment.CenterVertically),
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            if (isModifiable)
                TextButton(
                    text = getText("label.remove"),
                    onClick = {
                        onRemoveCallback(productDTO)
                    },
                    backgroundColor = Color.Red,
                    borderColor = Color.Red
                )
        }
    }
}