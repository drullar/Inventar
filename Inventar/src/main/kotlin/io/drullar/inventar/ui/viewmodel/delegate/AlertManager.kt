package io.drullar.inventar.ui.viewmodel.delegate

import io.drullar.inventar.ui.data.AlertType
import kotlinx.coroutines.flow.StateFlow

interface AlertManagerClient {
    fun getActiveAlert(): StateFlow<AlertType?>
}

interface AlertManager : AlertManagerClient {
    fun setActiveAlert(alertType: AlertType?)
}