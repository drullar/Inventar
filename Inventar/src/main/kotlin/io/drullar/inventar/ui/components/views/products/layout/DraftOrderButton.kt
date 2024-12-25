package io.drullar.inventar.ui.components.views.products.layout

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.utils.Icons

@Composable
fun DraftOrderButton(modifier: Modifier = Modifier, draftOrdersCount: String, onClick: () -> Unit) {
    Row {
        IconButton(
            onClick = onClick, modifier.wrapContentSize()
        ) {
            Icon(
                painter = painterResource(Icons.ORDER),
                contentDescription = "Draft orders: ",
                modifier = Modifier
            )
        }

        if (draftOrdersCount.isNotEmpty()) {
            Box(
                Modifier.background(Color.Red, CircleShape)
                    .widthIn(25.dp, 30.dp)
                    .wrapContentHeight()
            ) {
                Text(
                    text = draftOrdersCount,
                    fontSize = TextUnit(13f, TextUnitType.Sp),
                    color = Color.White,
                    modifier = modifier.align(Alignment.Center).padding(2.dp)
                )
            }
        }
    }
}

@Composable
@Preview
private fun DraftOrderButtonLimitPreview() {
    DraftOrderButton(draftOrdersCount = "99+", onClick = {})
}

@Composable
@Preview
private fun DraftOrderButtonSingleDitPreview() {
    DraftOrderButton(draftOrdersCount = "9", onClick = {})
}

@Composable
@Preview
private fun DraftOrderButtonEmptyPreview() {
    DraftOrderButton(draftOrdersCount = "", onClick = {})
}
