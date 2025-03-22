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
import io.drullar.inventar.persistence.schema.BARCODE_LENGTH
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.dialog.produceWarningText
import io.drullar.inventar.ui.components.field.FormInputField
import io.drullar.inventar.ui.components.field.IsNotEmpty
import io.drullar.inventar.ui.components.field.NotNegativeNumber
import io.drullar.inventar.ui.provider.getText

@Composable
fun ProductDetailedViewCard(
    productData: ProductDTO,
    onChange: () -> Unit,
    onSave: (ProductDTO) -> Unit,
    onRevert: () -> ProductDTO,
    modifier: Modifier = Modifier
) {
    var hasChange by remember { mutableStateOf(false) }
    var stateOfProductData by remember(key1 = productData.uid) { mutableStateOf(productData) }

    var nameFieldWarning by remember { mutableStateOf<String?>(null) }
    var sellingPriceFieldWarning by remember { mutableStateOf<String?>(null) }
    var availableQuantityFieldWarning by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxHeight().fillMaxWidth()) {
        val scrollableState = rememberScrollState()
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(),
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(state = scrollableState, orientation = Orientation.Vertical)
        ) {
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                FormInputField(
                    label = getText("field.name"),
                    defaultValue = stateOfProductData.name,
                    onValueChange = { value ->
                        hasChange = true
                        stateOfProductData = stateOfProductData.copy(name = value)
                        nameFieldWarning = produceWarningText(value, setOf(IsNotEmpty()))
                        onChange()
                    },
                    warningMessage = nameFieldWarning,
                    inputType = String::class
                )
                FormInputField(
                    label = getText("field.selling.price"),
                    defaultValue = stateOfProductData.sellingPrice.toString(),
                    onValueChange = { value ->
                        hasChange = true
                        stateOfProductData = stateOfProductData.copy(
                            sellingPrice = value.toBigDecimalOrNull() ?: 0.0.toBigDecimal()
                        )
                        sellingPriceFieldWarning =
                            produceWarningText(
                                stateOfProductData.sellingPrice,
                                setOf(IsNotEmpty(), NotNegativeNumber())
                            )
                        onChange()
                    },
                    warningMessage = sellingPriceFieldWarning,
                    inputType = Double::class
                )
                FormInputField(
                    label = "${getText("field.optional")} ${getText("field.provider.price")}",
                    defaultValue = stateOfProductData.providerPrice?.toString() ?: "",
                    onValueChange = {
                        hasChange = true
                        stateOfProductData =
                            stateOfProductData.copy(
                                providerPrice = it.toBigDecimalOrNull() ?: 0.0.toBigDecimal()
                            )
                        onChange()
                    },
                    inputType = Double::class
                )
                FormInputField(
                    label = getText("field.quantity"),
                    defaultValue = stateOfProductData.availableQuantity.toString(),
                    onValueChange = {
                        hasChange = true
                        stateOfProductData =
                            stateOfProductData.copy(availableQuantity = it.toIntOrNull() ?: 0)
                        availableQuantityFieldWarning =
                            produceWarningText(
                                stateOfProductData.availableQuantity,
                                setOf(IsNotEmpty(), NotNegativeNumber())
                            )
                        onChange()
                    },
                    inputType = Int::class,
                    warningMessage = availableQuantityFieldWarning
                )
                FormInputField(
                    label = "${getText("field.optional")} ${getText("field.barcode")}",
                    defaultValue = stateOfProductData.barcode ?: "",
                    onValueChange = {
                        hasChange = true
                        stateOfProductData = stateOfProductData.copy(barcode = it)
                        onChange()
                    },
                    inputType = String::class,
                    characterLimit = BARCODE_LENGTH
                )
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            if (hasChange) {
                Button(onClick = {
                    onSave(stateOfProductData)
                }) {
                    Text(getText("label.save"))
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(onClick = {
                    val originalData = onRevert()
                    stateOfProductData = originalData
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