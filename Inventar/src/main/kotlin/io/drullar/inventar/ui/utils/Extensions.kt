package io.drullar.inventar.ui.utils

import androidx.compose.ui.unit.Dp

fun percentageFromWindowHeight(percentage: Int, windowsHeight: Dp): Dp =
    (windowsHeight * percentage / 100)


fun percentageFromWindowWidth(percentage: Int, windowWidth: Dp): Dp =
    (windowWidth * percentage / 100)