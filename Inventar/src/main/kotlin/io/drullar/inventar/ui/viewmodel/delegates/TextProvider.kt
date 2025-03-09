package io.drullar.inventar.ui.viewmodel.delegates

import io.drullar.inventar.shared.SupportedLanguage

interface TextProvider {
    fun setActiveLanguage(language: SupportedLanguage)
    fun getText(textId: String): String
    fun getText(textId: String, pluggableValue: Any): String

    companion object {
        lateinit var singleton: TextProvider
    }
}

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
