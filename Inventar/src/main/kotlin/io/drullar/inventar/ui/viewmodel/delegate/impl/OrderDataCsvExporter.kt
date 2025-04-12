package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.persistence.repositories.impl.OrderRepository
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.ui.viewmodel.delegate.DataExporter
import io.drullar.inventar.utils.file.DataExportFile
import io.drullar.inventar.utils.file.ExportRequest
import java.util.Currency
import java.util.StringJoiner

class OrderDataCsvExporter(private val currency: Currency) : DataExporter<ExportRequest> {
    private val orderRepository = OrderRepository

    override fun export(request: ExportRequest) {
        val file = DataExportFile(request.targetDirectory).also {
            it.create()
            it.append(ORDER_DATA_CSV_KEYS.plus("\n"))
        }

        val orders = orderRepository.getAllByStatus(OrderStatus.COMPLETED).getOrThrow()
        orders.forEach {
            val linesToAppend = orderDataToCsvString(it)
            file.append(linesToAppend.joinToString("\n").plus("\n"))
        }

        println("Exported orders data to: ${file.getAbsolutePath()}")
    }

    private fun orderDataToCsvString(order: OrderDTO): List<String> =
        order.productToQuantity.map {
            val product = it.key
            val quantity = it.value
            StringJoiner(",")
                .add(order.orderId.toString())
                .add(order.creationDate.toString())
                .add(product.name)
                .add(quantity.toString())
                .add(order.getTotalPrice().toString())
                .add(currency.symbol)
                .toString()
        }

    companion object {
        const val ORDER_DATA_CSV_KEYS = "order,orderDate,productName,soldQuantity,orderSum,currency"
    }
}