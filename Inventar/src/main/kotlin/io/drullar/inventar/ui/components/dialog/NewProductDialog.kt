package io.drullar.inventar.ui.components.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import io.drullar.inventar.payload.ProductDetailedPayload

// TODO handle wrong input in numeric fields. Use supportingText and isError properties of TextField to provide necessary user message

@Composable
@Preview
fun NewDialogProduct(
    onClose: () -> Unit,
    onNewProductSubmit: (ProductDetailedPayload) -> Unit
) {
    val productForm = mutableStateOf(ProductDetailedPayload(""))

    DialogWindow(
        title = "Add New Product",
        resizable = false,
        onCloseRequest = { onClose() },
        state = rememberDialogState(WindowPosition(Alignment.Center), DpSize(500.dp, 500.dp)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            FormInputField(
                label = "Name",
                defaultValue = productForm.value.name,
                onValueChange = { productForm.value.name = it }
            )
            FormInputField(
                label = "Selling price",
                defaultValue = productForm.value.sellingPrice.toString(),
                onValueChange = { productForm.value.sellingPrice = it.toDouble() }
            )
            FormInputField(
                label = "Provider price",
                defaultValue = productForm.value.providerPrice.toString(),
                onValueChange = { productForm.value.providerPrice = it.toDouble() }
            )
            FormInputField(
                label = "Quantity",
                defaultValue = productForm.value.availableQuantity.toString(),
                onValueChange = { productForm.value.availableQuantity = it.toInt() }
            )
            FormInputField(
                label = "(Optional) Barcode",
                defaultValue = productForm.value.barcode ?: "",
                onValueChange = { productForm.value.barcode = it }
            )

            FilledTonalButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = productForm.value.name.isNotEmpty(),
                onClick = {
                    onNewProductSubmit(productForm.value)
                    onClose()
                }) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun FormInputField(
    label: String,
    defaultValue: String,
    onValueChange: (value: String) -> Unit
) {
    val mutableDefaultValue = remember { mutableStateOf(defaultValue) }
    val inputFieldModifier = Modifier.fillMaxWidth()
    TextField(
        value = mutableDefaultValue.value,
        label = { Text(label) },
        onValueChange = { value ->
            mutableDefaultValue.value = value
            onValueChange(value)
        },
        modifier = inputFieldModifier
    )
}
