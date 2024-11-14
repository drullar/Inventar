package io.drullar.inventar.ui.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductRow(productRowData: ProductRowData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .height(50.dp)
            .padding(0.dp, 5.dp)
            .roundedBorder(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text("#${productRowData.productId}", modifier = Modifier.padding(start = 5.dp))
        Text(productRowData.productName)
        Text("Left in stock: ${productRowData.quantityAvailable}")
        Text("Cost per item: ${productRowData.sellingPrice}", Modifier.padding(end = 5.dp))
    }
}

@Composable
private fun ProductField(text: String) {
    Text(text, modifier = Modifier.border(1.dp, Color.Black))
}

@Preview
@Composable
internal fun ProductRowPreviewContainer() {
    ProductRow(
        ProductRowData(
            "1",
            "Water bottle 350ml",
            10.5,
            10
        )
    )
}

data class ProductRowData(
    val productId: String,
    val productName: String,
    val sellingPrice: Double,
    val quantityAvailable: Int
)