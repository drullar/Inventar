package io.drullar.inventar.ui.components.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.shared.ProductDTO
import kotlin.reflect.KClass

@Composable
@Preview
fun NewProductDialog( //TODO reuse same form here and inside ProductDetailedPreviewCard
    onClose: () -> Unit,
    onSubmit: (ProductCreationDTO) -> Unit
) {
    val productForm by mutableStateOf(
        ProductCreationDTO(
            name = "",
            providerPrice = 0.0,
            barcode = ""
        )
    )

    var nameFieldWarning by remember { mutableStateOf<String?>(null) }

    DialogWindow(
        title = "Add New Product",
        resizable = false,
        onCloseRequest = { onClose() },
        state = rememberDialogState(WindowPosition(Alignment.Center), DpSize(500.dp, 500.dp)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            FormInputField(
                label = "Name",
                defaultValue = productForm.name,
                onValueChange = { value ->
                    productForm.name = value
                },
                warningMessage = nameFieldWarning,
                inputType = String::class
            )
            FormInputField(
                label = "Selling price",
                defaultValue = productForm.sellingPrice.toString(),
                onValueChange = { productForm.sellingPrice = it.toDouble() },
                inputType = Double::class
            )
            FormInputField(
                label = "Provider price",
                defaultValue = productForm.providerPrice.toString(),
                onValueChange = { productForm.providerPrice = it.toDouble() },
                inputType = Double::class
            )
            FormInputField(
                label = "Quantity",
                defaultValue = productForm.availableQuantity.toString(),
                onValueChange = { productForm.availableQuantity = it.toInt() },
                inputType = Int::class
            )
            FormInputField(
                label = "(Optional) Barcode",
                defaultValue = productForm.barcode ?: "",
                onValueChange = { productForm.barcode = it },
                inputType = String::class
            )

            FilledTonalButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    when (validateForm(productForm)) {
                        FormValidationProblem.NAME_IS_EMPTY -> {
                            nameFieldWarning = "Name must not be empty!"
                        }

                        FormValidationProblem.NONE -> {
                            onSubmit(productForm)
                            onClose()
                        }
                    }
                }) {
                Text("Save")
            }
        }
    }
}

private fun validateForm(form: ProductCreationDTO): FormValidationProblem = when {
    form.name.isEmpty() -> FormValidationProblem.NAME_IS_EMPTY
    else -> FormValidationProblem.NONE
}

@Composable
private fun <T : Any> FormInputField(
    label: String,
    defaultValue: String,
    inputType: KClass<T>,
    onValueChange: (value: String) -> Unit,
    warningMessage: String? = null
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
        modifier = inputFieldModifier,
        supportingText = {
            warningMessage?.let {
                Text(warningMessage, color = Color.Red)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = when (inputType) {
                Double::class -> KeyboardType.Decimal
                Number::class -> KeyboardType.Number
                else -> KeyboardType.Text
            }
        )
    )
}

private enum class FormValidationProblem {
    NAME_IS_EMPTY,
    NONE
}