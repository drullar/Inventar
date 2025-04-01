package io.drullar.inventar.ui.components.window.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
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
import io.drullar.inventar.isNumeric
import io.drullar.inventar.persistence.schema.BARCODE_LENGTH
import io.drullar.inventar.shared.ProductCreationDTO
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.field.FieldValidator
import io.drullar.inventar.ui.components.field.FormInputField
import io.drullar.inventar.ui.components.field.IsNotEmpty
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.utils.ContentDescription
import io.drullar.inventar.verifyValuesAreNotEmpty
import java.math.BigDecimal

@Composable
fun NewProductDialog( //TODO reuse same form here and inside ProductDetailedPreviewCard
    onClose: () -> Unit,
    onSubmit: (ProductCreationDTO) -> Unit
) {
    var nameField by remember { mutableStateOf("") }
    var sellingPriceField by remember { mutableStateOf("") }
    var providerPriceField by remember { mutableStateOf<String>("") }
    var availableQuantityField by remember { mutableStateOf("") }
    var barcodeField by remember { mutableStateOf("") }

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
                    defaultValue = nameField,
                    onValueChange = { value ->
                        nameFieldWarning = produceWarningText<String>(value, setOf(IsNotEmpty()))
                        nameField = value
                    },
                    warningMessage = nameFieldWarning,
                    inputType = String::class,
                    fieldSemanticDescription = ContentDescription.NEW_PRODUCT_NAME
                )
                FormInputField(
                    label = getText("field.selling.price"),
                    defaultValue = sellingPriceField,
                    onValueChange = {
                        sellingPriceField = it
                        sellingPriceFieldWarning = produceWarningText<String>(
                            it, setOf(IsNotEmpty())
                        )
                    },
                    warningMessage = sellingPriceFieldWarning,
                    inputType = Double::class,
                    fieldSemanticDescription = ContentDescription.NEW_PRODUCT_SELLING
                )
                FormInputField(
                    label = getText("field.optional") + " " + getText("field.provider.price"),
                    defaultValue = providerPriceField,
                    onValueChange = {
                        providerPriceField = it
                    },
                    inputType = Double::class,
                    fieldSemanticDescription = ContentDescription.NEW_PRODUCT_PROVIDER
                )
                FormInputField(
                    label = getText("field.quantity"),
                    defaultValue = availableQuantityField,
                    onValueChange = { changedValue ->
                        availableQuantityField =
                            if (changedValue.isBlank()) changedValue // allow empty string
                            else if (isNumeric(changedValue)) changedValue.toIntOrNull()?.toString()
                                ?: availableQuantityField // remove any floating points
                            else availableQuantityField
                        availableQuantityFieldWarning =
                            produceWarningText(changedValue, setOf(IsNotEmpty()))
                    },
                    inputType = Int::class,
                    warningMessage = availableQuantityFieldWarning,
                    fieldSemanticDescription = ContentDescription.NEW_PRODUCT_QUANTITY
                )
                FormInputField(
                    label = getText("field.optional") + " " + getText("field.barcode"),
                    defaultValue = barcodeField,
                    onValueChange = { barcodeField = it },
                    inputType = String::class,
                    characterLimit = BARCODE_LENGTH,
                    fieldSemanticDescription = ContentDescription.NEW_PRODUCT_BARCODE
                )
            }

            val isButtonEnabled = nameFieldWarning.isNullOrEmpty()
                    && sellingPriceFieldWarning.isNullOrEmpty() &&
                    verifyValuesAreNotEmpty(
                        nameField,
                        sellingPriceField,
                        availableQuantityField
                    )

            TextButton(
                text = getText("label.save"),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    onSubmit(
                        ProductCreationDTO(
                            name = nameField,
                            sellingPrice = sellingPriceField.toBigDecimalOrNull()
                                ?: BigDecimal.valueOf(0.0),
                            providerPrice = providerPriceField.toBigDecimalOrNull(),
                            barcode = barcodeField,
                            availableQuantity = availableQuantityField.toIntOrNull() ?: 0
                        )
                    )
                    onClose()
                },
                enabled = isButtonEnabled
            )
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
