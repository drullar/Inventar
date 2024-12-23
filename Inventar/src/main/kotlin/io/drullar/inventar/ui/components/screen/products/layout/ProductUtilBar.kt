package io.drullar.inventar.ui.components.screen.products.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductUtilBar(modifier: Modifier = Modifier, onNewProductButtonClick: () -> Unit) {
    Row(
        modifier = modifier
            .padding(5.dp)
    ) {
        Box(
            modifier = Modifier.padding(5.dp)
                .fillMaxWidth(0.7f)
                .fillMaxHeight()
                .roundedBorder()
        ) {
            FilledTonalButton(
                onClick = onNewProductButtonClick,
                Modifier.align(Alignment.CenterStart)
            ) {
                Text("Add new product")
            }
        }
    }
}