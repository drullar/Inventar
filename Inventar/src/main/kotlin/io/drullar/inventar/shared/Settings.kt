package io.drullar.inventar.shared

import java.util.Currency
import java.util.Locale

data class Settings(
    val currency: Currency = Currency.getInstance("BGN"),
    val language: SupportedLanguage = SupportedLanguage.ENGLISH
)

enum class SupportedLanguage(val locale: Locale) {
    ENGLISH(Locale.ENGLISH),
    BULGARIAN(Locale("BG"))
}