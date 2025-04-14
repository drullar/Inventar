package io.drullar.inventar.ui.data

import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.utils.file.DataExportFile

interface WindowPayload<T> {
    fun getData(): T
}

class EmptyPayload : WindowPayload<Unit> {
    override fun getData() = Unit
}

data class OrderWindowPayload(private val order: OrderDTO) : WindowPayload<OrderDTO> {
    override fun getData(): OrderDTO = order
}

data class ProductPayload(private val product: ProductDTO) : WindowPayload<ProductDTO> {
    override fun getData(): ProductDTO = product
}

data class ExportCompletionPayload(
    private val exportFile: DataExportFile
) : WindowPayload<DataExportFile> {
    override fun getData(): DataExportFile = exportFile
}

data class BarcodePayload(
    private val barcode: String
) : WindowPayload<String> {
    override fun getData(): String = barcode
}