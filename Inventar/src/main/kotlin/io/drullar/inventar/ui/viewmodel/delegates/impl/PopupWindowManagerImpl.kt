package io.drullar.inventar.ui.viewmodel.delegates.impl

import io.drullar.inventar.ui.data.EmptyPayload
import io.drullar.inventar.ui.viewmodel.delegates.PopupWindowManager
import io.drullar.inventar.ui.data.WindowPayload
import io.drullar.inventar.ui.data.WindowTypeInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PopupWindowManagerImpl<T : WindowTypeInterface> : PopupWindowManager<T> {

    private val activeWindow = MutableStateFlow<T?>(null)
    private var payload = MutableStateFlow<WindowPayload<*>>(EmptyPayload())

    override fun setActiveWindow(type: T?) {
        activeWindow.value = type
    }

    override fun <D> setActiveWindow(type: T?, payload: WindowPayload<D>) {
        setActiveWindow(type)
        if (type == null) setWindowPayload<Unit>(EmptyPayload())
        else setWindowPayload(payload)
    }

    override fun getActiveWindow(): StateFlow<T?> {
        return activeWindow
    }

    override fun <D> setWindowPayload(payload: WindowPayload<D>) {
        this.payload.value = payload
    }

    @Suppress("UNCHECKED_CAST")
    override fun <D> getWindowPayload(): StateFlow<WindowPayload<D>> {
        return payload.asStateFlow() as StateFlow<WindowPayload<D>>
    }

    override fun hasActiveWindow(): Boolean {
        return activeWindow.value != null
    }
}