package io.drullar.inventar.ui.components.screen.products.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductUtilBar(onNewProductButtonClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(5.dp)
            .fillMaxHeight(0.1f)
    ) {
        Box(
            modifier = Modifier.padding(5.dp)
                .fillMaxWidth(0.7f)
                .fillMaxHeight()
                .roundedBorder()
        ) {
            FilledTonalButton(onClick = onNewProductButtonClick) {
                Text("Add new product")
            }
        }
    }
}