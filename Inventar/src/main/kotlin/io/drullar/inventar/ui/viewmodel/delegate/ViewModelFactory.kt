package io.drullar.inventar.ui.viewmodel.delegate

import io.drullar.inventar.ui.components.navigation.NavigationDestination
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import io.drullar.inventar.ui.viewmodel.DefaultViewViewModel
import io.drullar.inventar.ui.viewmodel.OrderViewViewModel
import io.drullar.inventar.ui.viewmodel.SettingsViewModel
import io.drullar.inventar.ui.viewmodel.delegate.impl.AlertManagerImpl
import io.drullar.inventar.ui.viewmodel.delegate.impl.BarcodeScanManager
import io.drullar.inventar.ui.viewmodel.delegate.impl.OrdersDelegateImpl
import io.drullar.inventar.ui.viewmodel.delegate.impl.SettingsProviderImpl
import io.drullar.inventar.ui.viewmodel.delegate.impl.SharedAppStateDelegateImpl
import io.drullar.inventar.ui.viewmodel.delegate.impl.WindowManagerFacadeImpl
import io.drullar.inventar.utils.file.FileManager

class ViewModelFactory {
    val fileManager by lazy { FileManager() }
    val sharedAppStateHolder by lazy {
        SharedAppStateDelegateImpl(NavigationDestination.PRODUCTS_PAGE)
    }
    val alertManagerDelegate by lazy { AlertManagerImpl() }
    val settingsProvider by lazy { SettingsProviderImpl(fileManager) }
    val barcodeScanManager = BarcodeScanManager()
    val ordersDelegate = OrdersDelegateImpl()

    val defaultViewViewModel by lazy {
        DefaultViewViewModel(
            sharedAppStateHolder,
            alertManagerDelegate,
            settingsProvider,
            barcodeScanManager,
            ordersDelegate
        )
    }
    val orderViewViewModel by lazy {
        OrderViewViewModel(
            sharedAppStateHolder,
            settingsProvider,
            ordersDelegate
        )
    }
    val settingsViewModel by lazy { SettingsViewModel(settingsProvider) }
    val analyticsViewModel by lazy { AnalyticsViewModel(settingsProvider) }
    val globalWindowManager by lazy { WindowManagerFacadeImpl() }
}