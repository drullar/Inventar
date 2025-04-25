package io.drullar.inventar.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import java.util.Currency
import java.util.Locale

@Composable
fun OrderCreationCard(
    order: OrderDTO,
    onTerminate: () -> Unit,
    onComplete: (Boolean) -> Unit,
    onProductValueChange: (ProductDTO, Int) -> Unit,
    onProductRemove: (ProductDTO) -> Unit,
    renderContext: OrderDetailCardRenderContext,
    currency: Currency,
    locale: Locale
) {
    val productsMap = order.productToQuantity

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(1.dp, Color.Black, roundedBorderShape())
            .semantics { contentDescription = renderContext.buildContentDescription() },
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
                val isDraftOrder = order.status == OrderStatus.DRAFT
                LazyColumn(
                    Modifier.fillMaxWidth().fillMaxHeight(0.6f),
                    scrollState
                ) {
                    items(
                        items = productsMap.keys.toList(),
                        key = { it.uid }
                    ) { product ->
                        val quantity = productsMap[product]!!
                        OrderCreationRow(
                            productDTO = product,
                            isModifiable = isDraftOrder,
                            quantity = quantity,
                            onQuantityChangeCallback = { newQuantity ->
                                onProductValueChange(product, newQuantity)
                            },
                            onRemoveCallback = { onProductRemove(it) },
                            showQuantityWarning = (isDraftOrder && product.availableQuantity < quantity),
                            currency = currency,
                            locale = locale
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
                text = "${getText("field.total.price")}: ${order.getTotalPrice()} ${
                    currency.getSymbol(
                        locale
                    )
                }",
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.W100,
                fontSize = TextUnit(30f, TextUnitType.Sp)
            )
            Spacer(Modifier.padding(vertical = 10.dp))
            if (order.status == OrderStatus.DRAFT) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        getText("label.complete"),
                        { onComplete(hasQuantityIssue(productsMap)) }
                    )
                    TextButton(
                        getText("label.terminate"),
                        onTerminate,
                        backgroundColor = Color.White,
                        textColor = Color.Red,
                        borderColor = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCreationRow(
    productDTO: ProductDTO,
    isModifiable: Boolean,
    quantity: Int,
    showQuantityWarning: Boolean,
    onQuantityChangeCallback: (Int) -> Unit,
    onRemoveCallback: (ProductDTO) -> Unit,
    currency: Currency,
    locale: Locale
) {
    var currentQuantity: Int? by remember { mutableStateOf(quantity) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(quantity) {
        currentQuantity = quantity
    }
    LaunchedEffect(currentQuantity) {
        if (currentQuantity != null && currentQuantity != quantity) {
            onQuantityChangeCallback(currentQuantity!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black)
            .wrapContentHeight()
            .padding(5.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier) {
                BasicTextField(
                    value = currentQuantity?.toString() ?: "",
                    onValueChange = { value ->
                        if (isModifiable) {
                            if (value.isEmpty()) {
                                currentQuantity = null
                            } else value.toIntOrNull()?.let {
                                currentQuantity = it
                            }
                        }
                    },
                    enabled = isModifiable,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = TextStyle.Default.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .background(color = Colors.PlatinumGray)
                        .border(BorderStroke(0.1.dp, Color.Black), RoundedCornerShape(3.dp))
                        .widthIn(15.dp, 25.dp)
                        .align(Alignment.CenterVertically)
                        .wrapContentWidth()
                        .onFocusChanged { isFocused = it.isFocused }
                )
                Text(
                    "x",
                    Modifier.align(Alignment.CenterVertically).height(15.dp).padding(start = 2.dp)
                )
                Spacer(
                    modifier = Modifier.padding(horizontal = 5.dp).align(Alignment.CenterVertically)
                )
                Text(
                    productDTO.name,
                    modifier = Modifier.fillMaxWidth(0.4f)
                        .align(Alignment.CenterVertically),
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
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

        Text(
            text = getText(
                "label.price.per.unit",
                (productDTO.sellingPrice.toString() + currency.getSymbol(locale))
            ),
            modifier = Modifier.semantics {
                contentDescription = "${productDTO.name} price per unit"
            }
        )
        Text(
            text = StringBuilder(getText("field.total") + " ")
                .append(productDTO.sellingPrice.multiply(quantity.toBigDecimal()))
                .append(currency.getSymbol(locale))
                .toString(),
            fontWeight = FontWeight.W500,
            modifier = Modifier.semantics {
                contentDescription = "${productDTO.name} total order price"
            }
        )

        if (showQuantityWarning) {
            Text(
                text = getText("warning.product.quantity", productDTO.availableQuantity),
                color = Color.Red,
                modifier = Modifier.align(Alignment.Start).padding(top = 5.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Returns whether any specified product has a greater specified quantity that the available one
 */
private fun hasQuantityIssue(productsToQuantity: Map<ProductDTO, Int>) = productsToQuantity.any {
    val product = it.key
    val specifiedQuantity = it.value
    product.availableQuantity < specifiedQuantity
}

enum class OrderDetailCardRenderContext {
    PREVIEW,
    EXTERNAL_WINDOW;

    fun buildContentDescription() = "Order detail card in ${this.name}"
}