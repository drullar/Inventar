package io.drullar.inventar.ui.components.views.default.layout

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.button.Button
import io.drullar.inventar.ui.style.roundedBorder

@Composable
fun ProductUtilBar(modifier: Modifier = Modifier, onNewProductButtonClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(5.dp).border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .fillMaxWidth()
    ) {
//        Box(
//            modifier = Modifier.padding(5.dp)
//                .fillMaxWidth(0.7f)
//                .fillMaxHeight()
//                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
//        ) {
        Button(
            text = "New product",
            onClick = onNewProductButtonClick,
            modifier = Modifier.padding(start = 10.dp)
        )
//        }
    }
}

@Composable
@Preview
private fun ProductUtilBarPreview() {
    ProductUtilBar(Modifier.fillMaxHeight(0.1f), onNewProductButtonClick = {})
}