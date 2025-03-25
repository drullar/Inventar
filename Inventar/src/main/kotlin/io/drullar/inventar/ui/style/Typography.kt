package io.drullar.inventar.ui.style

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun appTypography(lineHeightOverride: Int? = null) = Typography(
    titleLarge = TextStyle(fontSize = 24.sp),
    titleMedium = TextStyle(fontSize = 22.sp),
    titleSmall = TextStyle(fontSize = 20.sp),
    bodyLarge = TextStyle(fontSize = 20.sp),
    bodyMedium = TextStyle(fontSize = 18.sp),
    bodySmall = TextStyle(fontSize = 16.sp),
    labelLarge = TextStyle(fontSize = 16.sp),
    labelMedium = TextStyle(fontSize = 15.sp),
    labelSmall = TextStyle(fontSize = 14.sp)
)

val Typography.highlightedLabelLarge: TextStyle
    get() = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W400)

val Typography.highlightedLabelMedium: TextStyle
    get() = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W400)

val Typography.highlightedLabelSmall: TextStyle
    get() = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W400)

val Typography.labelExtraSmall: TextStyle
    get() = TextStyle(fontSize = 10.sp)

val Typography.boldBodySmall: TextStyle
    get() = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)