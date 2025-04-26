package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.persistence.schema.BARCODE_LENGTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.ZoneOffset

interface BarcodeScanManagerInterface {
    fun append(character: Char)
    fun complete()
    fun getLastScanTime(): StateFlow<Long?>
    fun getLastScannedBarcode(): StateFlow<String>
}

class BarcodeScanManager : BarcodeScanManagerInterface {
    private var barcodeBuffer = ""
    private var lastScannedBarcode = MutableStateFlow("")
    private val lastScanTime = MutableStateFlow<Long?>(null)

    override fun append(character: Char) {
        if (barcodeBuffer.length == BARCODE_LENGTH) {
            complete()
            return
        }
        barcodeBuffer += character
    }

    override fun complete() {
        lastScannedBarcode.value = barcodeBuffer
        barcodeBuffer = ""
        lastScanTime.value = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    }

    override fun getLastScanTime(): StateFlow<Long?> {
        return lastScanTime
    }

    override fun getLastScannedBarcode(): StateFlow<String> {
        return lastScannedBarcode.also {
            barcodeBuffer = "" // clear buffer once this is collected
        }
    }
}