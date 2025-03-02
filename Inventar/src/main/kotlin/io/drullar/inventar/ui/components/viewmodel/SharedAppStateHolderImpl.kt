package io.drullar.inventar.ui.components.viewmodel

import io.drullar.inventar.ui.components.views.default.Preview
import kotlinx.coroutines.flow.MutableStateFlow

interface SharedAppStateHolder {
    fun getPreview(): MutableStateFlow<Preview<*>?>
    fun <T> setPreview(preview: Preview<T>?)
}

class SharedAppStateHolderImpl : SharedAppStateHolder {
    private var _preview = MutableStateFlow<Preview<*>?>(null)

    override fun getPreview(): MutableStateFlow<Preview<*>?> = _preview

    override fun <T> setPreview(preview: Preview<T>?) {
        _preview.value = preview
    }
}