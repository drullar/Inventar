package io.drullar.inventar.ui.components.cards

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.appTypography

@Composable
fun ProductSalesCard(product: ProductDTO, soldQuantity: Int, modifier: Modifier = Modifier) {
    OutlinedCard(modifier) {
        Text(
            text = "\"${product.name}\" sales",
            style = appTypography().titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        OutlinedTextField(
            value = soldQuantity.toString(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.align(Alignment.CenterHorizontally).wrapContentWidth(),
            textStyle = appTypography().titleLarge.copy(textAlign = TextAlign.Center)
        )
    }
}