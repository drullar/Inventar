package io.drullar.inventar.ui.viewmodel

import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.persistence.repositories.impl.ProductsRepository
import io.drullar.inventar.shared.PagedRequest
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.shared.SortingOrder
import io.drullar.inventar.ui.data.DialogWindowType
import io.drullar.inventar.ui.viewmodel.delegates.PopupWindowManager
import io.drullar.inventar.ui.viewmodel.delegates.SettingsProvider
import io.drullar.inventar.ui.viewmodel.delegates.impl.PopupWindowManagerImpl
import java.time.LocalDate

class AnalyticsViewModel(
    settingsProvider: SettingsProvider,
    windowManager: PopupWindowManager<DialogWindowType> = PopupWindowManagerImpl<DialogWindowType>()
) : SettingsProvider by settingsProvider,
    PopupWindowManager<DialogWindowType> by windowManager {

    private val productRepository = ProductsRepository
    private val orderRepository = OrderRepository

    fun searchForProduct(
        query: String,
        pagedRequest: PagedRequest<ProductsRepository.SortBy> = PagedRequest(
            0,
            10,
            SortingOrder.DESCENDING,
            ProductsRepository.SortBy.NAME
        )
    ) = productRepository.search(
        query,
        pagedRequest
    ).getOrThrow()

    fun getSalesForProduct(productDTO: ProductDTO, fromDate: LocalDate, untilDate: LocalDate) =
        orderRepository.getProductSoldAmount(productDTO.uid, fromDate, untilDate).getOrThrow()
}