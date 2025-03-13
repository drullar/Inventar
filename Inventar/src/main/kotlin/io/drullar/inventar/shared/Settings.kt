package io.drullar.inventar.shared

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Currency
import java.util.Locale

data class Settings(
    val defaultCurrency: Currency,
    val language: SupportedLanguage = SupportedLanguage.ENGLISH
) {
    constructor() : this(
        Currency.getInstance("EUR"),
        SupportedLanguage.ENGLISH
    )
}

enum class SupportedLanguage(val locale: Locale) {
    @JsonProperty("ENGLISH")
    ENGLISH(Locale.ENGLISH),

    @JsonProperty("BULGARIAN")
    BULGARIAN(Locale("BG"))
}