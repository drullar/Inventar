package io.drullar.inventar.ui.components.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.roundedBorderShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderProductConfirmation(
    product: ProductDTO,
    onConfirm: (Int) -> Unit,
    onCancel: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    BasicAlertDialog(
        onDismissRequest = onCancel,
        modifier = Modifier.border(1.dp, Color.Black, roundedBorderShape())
    ) {
        OutlinedCard(modifier = Modifier.wrapContentWidth().height(200.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Add \"${product.name}\" to an order?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxHeight(0.1f).align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier.wrapContentSize().fillMaxWidth(0.5f)
                        .align(alignment = Alignment.CenterHorizontally)
                ) {
                    TextField(
                        value = quantity.toString(),
                        onValueChange = { quantity = it.toInt() },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        shape = roundedBorderShape(),
                        modifier = Modifier.wrapContentWidth()
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = { onConfirm(quantity) }) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun OrderProductConfirmationPreview() {
    val product =
        ProductDTO(uid = 1, name = "New product", barcode = "", providerPrice = 0.0)
    OrderProductConfirmation(
        product = product,
        onConfirm = {},
        onCancel = {}
    )
}