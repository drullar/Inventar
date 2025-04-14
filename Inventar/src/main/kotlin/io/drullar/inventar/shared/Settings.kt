package io.drullar.inventar.shared

import com.fasterxml.jackson.annotation.JsonProperty
import io.drullar.inventar.ui.provider.getText
import java.util.Currency
import java.util.Locale

data class Settings(
    val defaultCurrency: Currency,
    val language: SupportedLanguage = SupportedLanguage.ENGLISH,
    val onScan: OnScan
) {
    constructor() : this(
        Currency.getInstance("EUR"),
        SupportedLanguage.ENGLISH,
        OnScan.ADD_TO_ORDER
    )
}

enum class SupportedLanguage(
    val locale: Locale,
    val localizedName: Lazy<String>
) {
    @JsonProperty("ENGLISH")
    ENGLISH(Locale.ENGLISH, lazy { "English" }),

    @JsonProperty("BULGARIAN")
    BULGARIAN(Locale("BG"), lazy { "Български" })
}

enum class OnScan(val text: Lazy<String>) {
    /**
     * Add to an order or add new product
     */
    ADD_TO_ORDER(lazy { getText("label.scanner.mode.additive") }),

    /**
     * Change product available quantity
     */
    RESTOCK(lazy { getText("label.scanner.mode.restock") })
}