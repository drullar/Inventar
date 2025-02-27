package io.drullar.inventar.ui.components.views.default.layout

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.utils.Icons

@Composable
fun DraftOrderButton(modifier: Modifier = Modifier, draftOrdersCount: Long, onClick: () -> Unit) {
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
        Box(
            Modifier.background(Color.Red, CircleShape)
                .widthIn(25.dp, 30.dp)
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (draftOrdersCount <= 99) draftOrdersCount.toString() else "99+",
                fontSize = TextUnit(13f, TextUnitType.Sp),
                textAlign = TextAlign.Center,
                maxLines = 1,
                color = Color.White,
//                modifier = modifier.align(Alignment.Center)//.padding(5.dp)
            )
        }
    }
}

@Composable
@Preview
private fun DraftOrderButtonLimitPreview() {
    DraftOrderButton(draftOrdersCount = 99, onClick = {})
}

@Composable
@Preview
private fun DraftOrderButtonSingleDitPreview() {
    DraftOrderButton(draftOrdersCount = 1, onClick = {})
}

@Composable
@Preview
private fun DraftOrderButtonEmptyPreview() {
    DraftOrderButton(draftOrdersCount = 0, onClick = {})
}
