package io.drullar.inventar.ui.viewmodel.delegates.impl

import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.data.WindowPayload
import io.drullar.inventar.ui.data.ExternalWindowType
import io.drullar.inventar.ui.viewmodel.delegates.PopupWindowManager
import io.drullar.inventar.ui.viewmodel.delegates.WindowManagerFacade
import kotlinx.coroutines.flow.StateFlow

class WindowManagerFacadeImpl(
    private val dialogWindowManager: PopupWindowManager<DialogWindowType> = PopupWindowManagerImpl(),
    private val externalWindowManager: PopupWindowManager<ExternalWindowType> = PopupWindowManagerImpl()
) : WindowManagerFacade {

    override fun <T> setActiveDialog(
        dialogWindowType: DialogWindowType?,
        payload: WindowPayload<T>?
    ) {
        dialogWindowManager.setActiveWindow(dialogWindowType, payload)
    }

    override fun getActiveDialog(): StateFlow<DialogWindowType?> {
        return dialogWindowManager.getActiveWindow()
    }

    override fun <T> getActiveDialogPayload(): WindowPayload<T> {
        return dialogWindowManager.getWindowPayload()
    }

    override fun <T> setActiveWindow(dialogType: ExternalWindowType?, payload: WindowPayload<T>?) {
        externalWindowManager.setActiveWindow(dialogType, payload)
    }

    override fun hasActiveDialogWindow(): Boolean {
        return dialogWindowManager.hasActiveWindow()
    }

    override fun getActiveWindow(): StateFlow<ExternalWindowType?> {
        return externalWindowManager.getActiveWindow()
    }

    override fun <T> getActiveWindowPayload(): WindowPayload<T> {
        return externalWindowManager.getWindowPayload()
    }

    override fun hasActiveExternalWindow(): Boolean {
        return externalWindowManager.hasActiveWindow()
    }
}