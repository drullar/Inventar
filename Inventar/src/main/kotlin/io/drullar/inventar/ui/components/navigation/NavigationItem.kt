package io.drullar.inventar.ui.components.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.utils.Icons

@Composable
fun NavigationItem(
    iconPath: String = Icons.PRODUCTS_ICON,
    textIdentifier: String,
    onClick: () -> Unit
) {
    val navItemWidth = 150
    val navItemHeight = 40

    Row(modifier = Modifier
        .clickable { onClick.invoke() }
        .size(navItemWidth.dp, navItemHeight.dp)
        .border(1.dp, Color.Black, RoundedCornerShape(10.dp))
    ) {
//        Image(
//            bitmap = Res.
//            textIdentifier,
//            modifier = Modifier
//                .size((navItemWidth / 2).dp, navItemHeight.dp)
//                .padding((PaddingValues.Absolute(left = 5.dp))),
//            alignment = Alignment.BottomStart
//        )
        Text(
            textIdentifier,
            textAlign = TextAlign.Center,
            fontSize = TextUnit(10f, TextUnitType.Unspecified),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}