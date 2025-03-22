package io.drullar.inventar.unit.delegates

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.ui.provider.TextProvider
import io.drullar.inventar.ui.provider.impl.TextProviderImpl
import io.drullar.inventar.ui.provider.getText
import org.junit.*

class TestTextProvider {

    lateinit var textProvider: TextProvider

    @Before
    fun init() {
        textProvider = TextProviderImpl(SupportedLanguage.ENGLISH)
    }

    @Test
    fun getText() {
        val settingsLabel = textProvider.getText("label.settings")
        assertThat(settingsLabel).isEqualTo("Settings")

        val addProductText = textProvider.getText("product.add", "Cola")
        assertThat(addProductText).isEqualTo("Add \"Cola\" to an order")
    }

    @Test
    fun switchLanguage() {
        val settingsLabel = textProvider.getText("label.settings")
        assertThat(settingsLabel).isEqualTo("Settings")
        textProvider.setActiveLanguage(SupportedLanguage.BULGARIAN)
        val settingsLabelBulgarian = textProvider.getText("label.settings")
        assertThat(settingsLabelBulgarian).isEqualTo("Настройки")

        val addProductText = textProvider.getText("product.add", "Кола")
        assertThat(addProductText).isEqualTo("Добави \"Кола\" към поръчка")
    }

    @Test
    fun getTextExtensionFunction() {
        TextProviderImpl(SupportedLanguage.ENGLISH)
        assertThat(getText("label.settings")).isEqualTo("Settings")
    }
}