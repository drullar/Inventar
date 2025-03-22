package io.drullar.inventar.ui.provider

import io.drullar.inventar.shared.SupportedLanguage

interface TextProvider {
    fun setActiveLanguage(language: SupportedLanguage)
    fun getText(textId: String): String
    fun getText(textId: String, pluggableValue: Any): String

    companion object {
        lateinit var singleton: TextProvider
    }
}