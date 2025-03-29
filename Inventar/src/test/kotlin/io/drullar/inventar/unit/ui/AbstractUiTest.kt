package io.drullar.inventar.unit.ui

import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.ui.provider.TextProvider
import io.drullar.inventar.ui.provider.impl.TextProviderImpl
import org.junit.BeforeClass

abstract class AbstractUiTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun initDependencies() {
            TextProvider.singleton = TextProviderImpl(activeLanguage = SupportedLanguage.ENGLISH)
        }
    }
}