package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.ui.viewmodel.delegates.SettingsProvider

class SettingsViewModel(settingsProvider: SettingsProvider) : SettingsProvider by settingsProvider