package io.drullar.inventar.ui.components.cards

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.isNumeric
import io.drullar.inventar.persistence.schema.BARCODE_LENGTH
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.window.dialog.produceWarningText
import io.drullar.inventar.ui.components.field.FormInputField
import io.drullar.inventar.ui.components.field.IsNotEmpty
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.utils.ContentDescription
import java.math.BigDecimal

@Composable
fun ProductDetailedViewCard(
    productData: ProductDTO,
    onChange: () -> Unit, // TODO use initialProductData to compare if there is change
    onSave: (ProductDTO) -> Unit,
    onRevert: () -> ProductDTO,
    modifier: Modifier = Modifier
) {
    val initialProductData = remember { mutableStateOf(productData) }

    var nameField by remember { mutableStateOf(productData.name) }
    var sellingPriceField by remember { mutableStateOf(productData.sellingPrice.toString()) }
    var providerPriceField by remember {
        mutableStateOf(
            productData.providerPrice?.toString() ?: ""
        )
    }
    var availableQuantityField by remember { mutableStateOf(productData.availableQuantity.toString()) }
    var barcodeField by remember { mutableStateOf(productData.barcode ?: "") }
    var nameFieldWarning by remember { mutableStateOf<String?>(null) }
    var sellingPriceFieldWarning by remember { mutableStateOf<String?>(null) }
    var availableQuantityFieldWarning by remember { mutableStateOf<String?>(null) }

    var hasChange by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxHeight().fillMaxWidth()) {
        val scrollableState = rememberScrollState()
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(),
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(state = scrollableState, orientation = Orientation.Vertical)
        ) {
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                    FormInputField(
                        label = getText("field.name"),
                        defaultValue = nameField,
                        onValueChange = { value ->
                            nameFieldWarning = produceWarningText(value, setOf(IsNotEmpty()))
                            nameField = value
                            if (nameField != productData.name) hasChange = true
                        },
                        warningMessage = nameFieldWarning,
                        inputType = String::class,
                        fieldSemanticDescription = ContentDescription.EDIT_PRODUCT_NAME
                    )
                    FormInputField(
                        label = getText("field.selling.price"),
                        defaultValue = sellingPriceField,
                        onValueChange = {
                            sellingPriceField = it
                            sellingPriceFieldWarning = produceWarningText(
                                it, setOf(IsNotEmpty())
                            )
                            if (sellingPriceField != productData.sellingPrice.toString())
                                hasChange = true
                        },
                        warningMessage = sellingPriceFieldWarning,
                        inputType = Double::class,
                        fieldSemanticDescription = ContentDescription.EDIT_PRODUCT_SELLING
                    )
                    FormInputField(
                        label = getText("field.optional") + " " + getText("field.provider.price"),
                        defaultValue = providerPriceField,
                        onValueChange = {
                            providerPriceField = it
                            if (providerPriceField != productData.providerPrice.toString())
                                hasChange = true
                        },
                        inputType = Double::class,
                        fieldSemanticDescription = ContentDescription.EDIT_PRODUCT_PROVIDER
                    )
                    FormInputField(
                        label = getText("field.quantity"),
                        defaultValue = availableQuantityField,
                        onValueChange = { changedValue ->
                            availableQuantityField =
                                if (changedValue.isBlank()) changedValue // allow empty string
                                else if (isNumeric(changedValue)) changedValue.toIntOrNull()
                                    ?.toString()
                                    ?: availableQuantityField // remove any floating points
                                else availableQuantityField
                            availableQuantityFieldWarning =
                                produceWarningText(changedValue, setOf(IsNotEmpty()))

                            if (availableQuantityField != productData.availableQuantity.toString())
                                hasChange = true
                        },
                        inputType = Int::class,
                        warningMessage = availableQuantityFieldWarning,
                        fieldSemanticDescription = ContentDescription.EDIT_PRODUCT_QUANTITY
                    )
                    FormInputField(
                        label = getText("field.optional") + " " + getText("field.barcode"),
                        defaultValue = barcodeField,
                        onValueChange = {
                            barcodeField = it
                            if (barcodeField != productData.barcode)
                                hasChange = true
                        },
                        inputType = String::class,
                        characterLimit = BARCODE_LENGTH,
                        fieldSemanticDescription = ContentDescription.EDIT_PRODUCT_BARCODE
                    )
                }
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            if (hasChange) {
                Button(onClick = {
                    onSave(
                        productData.copy(
                            name = nameField,
                            sellingPrice = BigDecimal(sellingPriceField),
                            providerPrice = BigDecimal(sellingPriceField),
                            availableQuantity = availableQuantityField.toInt(),
                            barcode = barcodeField
                        )
                    )
                }) {
                    Text(getText("label.save"))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(onClick = {
                    val originalData = onRevert()
                    availableQuantityField = originalData.availableQuantity.toString()
                    nameField = originalData.name
                    sellingPriceField = originalData.sellingPrice.toString()
                    providerPriceField = originalData.providerPrice?.toString() ?: ""
                    barcodeField = originalData.barcode ?: ""
                }) {
                    Text(getText("label.revert"))
                }
            }
        }
    }
}

@Composable
@Preview
private fun ProductDetailedViewCardPreview() {
    ProductDetailedViewCard(
        ProductDTO(uid = 1, name = "productName", barcode = "92319321"),
        {},
        {},
        { ProductDTO(1, "") })
}