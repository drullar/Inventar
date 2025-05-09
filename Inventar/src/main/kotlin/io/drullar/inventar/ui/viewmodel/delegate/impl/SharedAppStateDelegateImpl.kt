package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.viewmodel.delegate.SharedAppStateDelegate
import io.drullar.inventar.ui.data.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedAppStateDelegateImpl(
    initialNavigationDestination: NavigationDestination
) : SharedAppStateDelegate {
    private val preview = MutableStateFlow<Preview<*>?>(null)
    private var navigationDestination = MutableStateFlow(initialNavigationDestination)

    override fun getPreview(): MutableStateFlow<Preview<*>?> = preview

    override fun <T> setPreview(preview: Preview<T>?) {
        this.preview.value = preview
    }

    override fun getNavigationDestination(): StateFlow<NavigationDestination> =
        navigationDestination

    override fun setNavigationDestination(navigationDestination: NavigationDestination) {
        this.navigationDestination.value = navigationDestination
    }
}