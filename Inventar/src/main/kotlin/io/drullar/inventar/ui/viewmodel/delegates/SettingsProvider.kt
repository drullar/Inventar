package io.drullar.inventar.ui.viewmodel.delegates

import io.drullar.inventar.shared.Settings
import kotlinx.coroutines.flow.StateFlow

interface SettingsProvider {
    fun getSettings(): StateFlow<Settings>
    fun setSettings(settings: Settings)
}