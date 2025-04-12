package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.ui.viewmodel.delegate.AlertManager
import io.drullar.inventar.ui.data.AlertType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AlertManagerImpl : AlertManager {
    private var activeAlert = MutableStateFlow<AlertType?>(null)

    override fun setActiveAlert(alertType: AlertType?) {
        activeAlert.value = alertType
    }

    override fun getActiveAlert(): StateFlow<AlertType?> = activeAlert

}