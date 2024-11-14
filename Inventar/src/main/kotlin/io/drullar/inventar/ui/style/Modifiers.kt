package io.drullar.inventar.ui.style

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.roundedBorder() = this.border(
    1.dp,
    Color.Black,
    rounderBorderShape()
)
