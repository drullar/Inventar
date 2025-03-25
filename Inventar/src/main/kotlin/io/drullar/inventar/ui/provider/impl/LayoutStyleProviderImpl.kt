package io.drullar.inventar.ui.provider.impl

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.provider.LayoutStyleProvider
import io.drullar.inventar.ui.style.LayoutStyle

class LayoutStyleProviderImpl(windowSize: DpSize) : LayoutStyleProvider {

    private val activeLayoutStyle =
        if (windowSize.width >= 1920.dp) LayoutStyle.NORMAL else LayoutStyle.COMPACT

    override fun getActiveStyle(): LayoutStyle = activeLayoutStyle

    init {
        LayoutStyleProvider.singleton = this
    }
}