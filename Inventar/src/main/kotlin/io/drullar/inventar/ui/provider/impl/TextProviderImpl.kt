package io.drullar.inventar.ui.provider.impl

import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.ui.provider.TextProvider
import io.drullar.inventar.ui.provider.TextProvider.Companion.singleton
import java.util.ResourceBundle

class TextProviderImpl(
    private var activeLanguage: SupportedLanguage
) : TextProvider {

    init {
        singleton = this
    }

    private var resourceBundle = ResourceBundle.getBundle("text", activeLanguage.locale)

    override fun setActiveLanguage(language: SupportedLanguage) {
        activeLanguage = language
        resourceBundle = ResourceBundle.getBundle("text", language.locale)
    }

    override fun getText(textId: String): String =
        resourceBundle.getString(textId.trim())

    override fun getText(textId: String, pluggableValue: Any): String =
        getText(textId.trim()).format(pluggableValue)
}