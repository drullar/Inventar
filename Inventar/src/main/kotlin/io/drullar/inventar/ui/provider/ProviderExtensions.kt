package io.drullar.inventar.ui.provider

import io.drullar.inventar.ui.style.LayoutStyle

fun getText(textId: String) = try {
    TextProvider.singleton.getText(textId)
} catch (_: UninitializedPropertyAccessException) {
    "Text provider error" // Needed because of unit tests and the use of getText in some enums
}

fun getText(textId: String, pluggableValue: Any) = try {
    TextProvider.singleton.getText(textId, pluggableValue)
} catch (_: UninitializedPropertyAccessException) {
    "Text provider error" // Needed because of unit tests and the use of getText in some enums
}

fun getLayoutStyle(): LayoutStyle = LayoutStyleProvider.singleton.getActiveStyle()