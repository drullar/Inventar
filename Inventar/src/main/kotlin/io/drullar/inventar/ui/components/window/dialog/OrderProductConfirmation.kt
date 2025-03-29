package io.drullar.inventar.ui.components.window.dialog

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.provider.getText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderProductConfirmation(
    product: ProductDTO,
    initialQuantity: Int,
    onConfirm: (Int) -> Unit,
    onCancel: () -> Unit
) {
    var quantity by remember { mutableStateOf(initialQuantity) }

    BasicAlertDialog(
        onDismissRequest = onCancel,
        modifier = Modifier.border(1.dp, Color.Black, roundedBorderShape())
            .semantics {
                contentDescription =
                    "Dialog that allows you to specify the amount of a given product to add to an order"
            }
    ) {
        OutlinedCard(modifier = Modifier.wrapContentWidth().height(200.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    getText("product.add", product.name) + "?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxHeight(0.1f).align(Alignment.CenterHorizontally)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.wrapContentSize().fillMaxWidth(0.5f)
                        .align(alignment = Alignment.CenterHorizontally)
                ) {
                    TextField(
                        value = if (quantity > 0) quantity.toString() else "",
                        onValueChange = {
                            quantity = if (it.isEmpty()) 0 else it.toIntOrNull() ?: quantity
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        shape = roundedBorderShape(),
                        modifier = Modifier.widthIn(30.dp, 70.dp)
                            .semantics {
                                contentDescription =
                                    "Test field to input quantity which is to be added to the order"
                            },
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextButton(
                        text = getText("label.cancel"),
                        onClick = onCancel,
                        backgroundColor = Color.White,
                        textColor = Color.Red,
                        borderColor = Color.Red
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    TextButton(
                        text = getText("label.add"),
                        onClick = { onConfirm(quantity) }
                    )
                }
            }
        }
    }
}