package io.drullar.inventar.ui.viewmodel.delegates.impl

import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.ui.viewmodel.delegates.TextProvider
import io.drullar.inventar.ui.viewmodel.delegates.TextProvider.Companion.singleton
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
        resourceBundle.getString(textId)

    override fun getText(textId: String, pluggableValue: Any): String =
        getText(textId).format(pluggableValue)
}