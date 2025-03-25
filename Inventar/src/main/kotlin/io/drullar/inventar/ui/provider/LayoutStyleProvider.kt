package io.drullar.inventar.ui.provider

import io.drullar.inventar.ui.style.LayoutStyle

/**
 * Abstraction that supplies the active [LayoutStyle]
 */
interface LayoutStyleProvider {
    fun getActiveStyle(): LayoutStyle

    companion object {
        lateinit var singleton: LayoutStyleProvider
    }
}