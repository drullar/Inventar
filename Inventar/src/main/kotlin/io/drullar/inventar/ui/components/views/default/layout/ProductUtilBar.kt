package io.drullar.inventar.ui.components.views.default.layout

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.viewmodel.delegates.getText

@Composable
fun ProductUtilBar(modifier: Modifier = Modifier, onNewProductButtonClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(5.dp) //.border(1.dp, Color.Black, RoundedCornerShape(4.dp))
            .fillMaxWidth(0.7f)
    ) {
        TextButton(
            text = getText("product.new"),
            onClick = onNewProductButtonClick,
            modifier = Modifier.padding(start = 10.dp),
            backgroundColor = Colors.DarkGreen,
            borderColor = Colors.DarkGreen
        )
    }
}

@Composable
@Preview
private fun ProductUtilBarPreview() {
    ProductUtilBar(Modifier.fillMaxHeight(0.1f), onNewProductButtonClick = {})
}