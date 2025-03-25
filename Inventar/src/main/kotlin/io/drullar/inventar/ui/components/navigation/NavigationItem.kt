package io.drullar.inventar.ui.components.navigation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.style.roundedBorder
import io.drullar.inventar.ui.style.roundedBorderShape

@Preview
@Composable
fun NavigationItem(
    details: NavigationItemDetails, isSelected: Boolean, onClick: () -> Unit
) {
    val navItemHeight = 40.dp
    val onSelectBgColor = Colors.BABY_BLUE
    val defaultBgColor = Color.White

    Row(
        modifier = Modifier
            .clickable(enabled = !isSelected) { onClick() }
            .height(navItemHeight)
            .wrapContentWidth()
            .roundedBorder()
            .wrapContentWidth()
            .background(
                color = if (isSelected) onSelectBgColor else defaultBgColor,
                shape = roundedBorderShape()
            )
    ) {
        Image(
            painter = painterResource(details.iconPath), //painterResource(details.iconPath, CustomResourceLoader()),
            contentDescription = details.textIdentifier,
            modifier = Modifier
                .size(30.dp, navItemHeight * 2 / 3)
                .align(Alignment.CenterVertically)
                .padding((PaddingValues.Absolute(left = 5.dp))),
            alignment = Alignment.BottomStart
        )
        Text(
            text = details.textIdentifier,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 10.dp),
            textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None,
            style = appTypography().labelMedium
        )
    }
}