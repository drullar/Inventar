package io.drullar.inventar.ui.components.cards

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.drullar.inventar.toMutableStateMap
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.utils.Icons

/**
 * [products] - map of product name to the amount from that product
 */
@Composable
fun OrderCreationCard(products: Map<String, Int>, orderId: Int) {
    var selectedRowIndex by remember { mutableStateOf<Int?>(null) }
    var productState = remember { products.toMutableStateMap() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .border(1.dp, Color.Black, roundedBorderShape())
    ) {
        Text(
            "Order #${orderId}",
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = TextUnit(24f, TextUnitType.Sp)
        )
        Spacer(Modifier.padding(10.dp))

        LazyColumn(
            contentPadding = PaddingValues(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(productState.keys.toList()) { rowIndex, productName ->
                OrderCreationRow(
                    productName = productName,
                    quantity = products[productName]!!,
                    onSelectCallback = { selectedRowIndex = rowIndex },
                    onQuantityChangeCallback = { newQuantity ->
                        productState[productName] = newQuantity
                    },
                    onRemoveCallback = {
                        productState.remove(productName)
                    },
                )
            }
        }
    }
}

@Composable
private fun OrderCreationRow(
    productName: String,
    quantity: Int,
    onSelectCallback: () -> Unit,
    onQuantityChangeCallback: (Int) -> Unit,
    onRemoveCallback: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 2.dp)
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
                    .height(15.dp).widthIn(15.dp, 50.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                "x",
                Modifier.align(Alignment.CenterVertically).height(15.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 5.dp).align(Alignment.CenterVertically))
            Text(
                productName,
                modifier = Modifier.fillMaxWidth(0.8f)
                    .align(Alignment.CenterVertically)
                    .height(15.dp),
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
            IconButton(onClick = onRemoveCallback) {
                Icon(
                    painterResource(Icons.CROSS_RED_ICON),
                    "remove from order",
                    Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
@Preview
private fun OrderCreationCardPreview() {
    OrderCreationCard(
        mapOf(
            "NewProduct1" to 1,
            "OldProduct" to 12
        ),
        1
    )
}