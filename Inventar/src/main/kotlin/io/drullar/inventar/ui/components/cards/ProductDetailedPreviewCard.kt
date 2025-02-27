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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.roundedBorderShape

/**
 * Card used to view full details of a product and edit the field as needed
 * [productData] - the data that is going to be displayed
 * [onChange] - called when a field has changes
 * [onTerminalChange] - called when a Save or Revert buttons a clicked
 */
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

    Column(modifier = modifier.fillMaxHeight().fillMaxWidth()) {
        val scrollableState = rememberScrollState()
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(),
            modifier = Modifier
                .fillMaxWidth()
                .scrollable(state = scrollableState, orientation = Orientation.Vertical)
        ) {
            // TODO add icon
            Field(
                label = "Name",
                value = stateOfProductData.name
            ) { value ->
                hasChange = true
                stateOfProductData = stateOfProductData.copy(name = value)
                onChange()
            }

            Field(
                label = "Selling price",
                value = stateOfProductData.sellingPrice.toString()
            ) { value ->
                hasChange = true
                stateOfProductData = stateOfProductData.copy(sellingPrice = value.toDouble())
                onChange()
            }

            Field(
                label = "Available quantity",
                value = stateOfProductData.availableQuantity.toString()
            ) { value ->
                hasChange = true
                stateOfProductData = stateOfProductData.copy(availableQuantity = value.toInt())
                onChange()
            }

            Field(
                label = "Provider price",
                value = stateOfProductData.providerPrice.toString()
            ) { value ->
                hasChange = true
                stateOfProductData.providerPrice = stateOfProductData.providerPrice
                onChange()
            }

            Field(
                label = "Barcode",
                value = stateOfProductData.barcode ?: ""
            ) { value ->
                hasChange = true
                stateOfProductData.barcode = value // TODO
                onChange()
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            if (hasChange) {
                Button(onClick = {
                    onSave(stateOfProductData)
                }) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(20.dp))
                Button(onClick = {
                    val originalData = onRevert()
                    stateOfProductData = originalData
                }) {
                    Text("Revert")
                }
            }
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onChange,
        shape = roundedBorderShape(),
        label = { Text(label) },
        colors = TextFieldDefaults.colors().copy(unfocusedContainerColor = Color.White),

        )
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