package io.drullar.inventar.ui.viewmodel.delegates.impl

import io.drullar.inventar.ui.viewmodel.delegates.AlertManager
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