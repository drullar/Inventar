package io.drullar.inventar.ui.provider

import io.drullar.inventar.ui.style.AppStyle

interface AppStyleProvider {
    fun getActiveStyle(): AppStyle

    companion object {
        lateinit var singleton: AppStyleProvider
    }
}