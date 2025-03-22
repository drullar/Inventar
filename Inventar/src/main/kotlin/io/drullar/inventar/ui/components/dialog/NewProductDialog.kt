package io.drullar.inventar.ui.components.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import io.drullar.inventar.persistence.schema.BARCODE_LENGTH
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.ui.components.field.FieldValidator
import io.drullar.inventar.ui.components.field.FormInputField
import io.drullar.inventar.ui.components.field.IsNotEmpty
import io.drullar.inventar.ui.components.field.NotNegativeNumber
import io.drullar.inventar.ui.provider.getText
import java.math.BigDecimal

@Composable
fun NewProductDialog( //TODO reuse same form here and inside ProductDetailedPreviewCard
    onClose: () -> Unit,
    onSubmit: (ProductCreationDTO) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf(BigDecimal(0.0)) }
    var providerPrice by remember { mutableStateOf<BigDecimal?>(null) }
    var availableQuantity by remember { mutableStateOf(0) }
    var barcode by remember { mutableStateOf("") }

    var nameFieldWarning by remember { mutableStateOf<String?>(null) }
    var sellingPriceFieldWarning by remember { mutableStateOf<String?>(null) }
    var availableQuantityFieldWarning by remember { mutableStateOf<String?>(null) }

    DialogWindow(
        title = getText("product.new"),
        resizable = false,
        onCloseRequest = { onClose() },
        state = rememberDialogState(WindowPosition(Alignment.Center), DpSize(500.dp, 500.dp)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                FormInputField(
                    label = getText("field.name"),
                    defaultValue = name,
                    onValueChange = { value ->
                        nameFieldWarning = produceWarningText(value, setOf(IsNotEmpty()))
                        name = value
                    },
                    warningMessage = nameFieldWarning,
                    inputType = String::class
                )
                FormInputField(
                    label = getText("field.selling.price"),
                    defaultValue = sellingPrice.toString(),
                    onValueChange = {
                        sellingPrice = it.toBigDecimalOrNull() ?: BigDecimal.valueOf(0.0)
                        sellingPriceFieldWarning =
                            produceWarningText<BigDecimal>(
                                sellingPrice, setOf(IsNotEmpty(), NotNegativeNumber())
                            )
                    },
                    warningMessage = sellingPriceFieldWarning,
                    inputType = Double::class
                )
                FormInputField(
                    label = getText("field.optional") + " " + getText("field.provider.price"),
                    defaultValue = providerPrice?.toString() ?: "",
                    onValueChange = { providerPrice = it.toBigDecimalOrNull() ?: BigDecimal(0.0) },
                    inputType = Double::class
                )
                FormInputField(
                    label = getText("field.quantity"),
                    defaultValue = availableQuantity.toString(),
                    onValueChange = {
                        availableQuantity = it.toIntOrNull() ?: 0
                        availableQuantityFieldWarning =
                            produceWarningText(
                                availableQuantity,
                                setOf(IsNotEmpty(), NotNegativeNumber())
                            )
                    },
                    inputType = Int::class,
                    warningMessage = availableQuantityFieldWarning
                )
                FormInputField(
                    label = getText("field.optional") + " " + getText("field.barcode"),
                    defaultValue = barcode,
                    onValueChange = { barcode = it },
                    inputType = String::class,
                    characterLimit = BARCODE_LENGTH
                )
            }

            FilledTonalButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    if (nameFieldWarning.isNullOrEmpty() && sellingPriceFieldWarning.isNullOrEmpty()) {
                        onSubmit(
                            ProductCreationDTO(
                                name = name,
                                sellingPrice = sellingPrice,
                                providerPrice = providerPrice
                            )
                        )
                        onClose()
                    }
                    //TODO else some visual queue to acknowledge the validation errors
                }) {
                Text(getText("label.save"))
            }
        }
    }
}

private fun concatValidationIssues(issueTexts: List<String>) = issueTexts.joinToString(" ")
fun <T> produceWarningText(
    value: T,
    validators: Set<FieldValidator<T>>
): String? {
    val validationIssues = FieldValidator.validate(value, validators)
        .map { it.validationErrorMessage() }
    return if (validationIssues.isNotEmpty()) concatValidationIssues(validationIssues)
    else null
}

@Composable
@Preview
private fun NewProductDialogPreview() {
    NewProductDialog({}, {})
}