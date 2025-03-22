package io.drullar.inventar.ui.provider.impl

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.provider.AppStyleProvider
import io.drullar.inventar.ui.style.AppStyle

class AppStyleProviderImpl(windowSize: DpSize) : AppStyleProvider {

    private val activeAppStyle =
        if (windowSize.width >= 1250.dp) AppStyle.NORMAL else AppStyle.COMPACT

    override fun getActiveStyle(): AppStyle = activeAppStyle

    init {
        AppStyleProvider.singleton = this
    }
}