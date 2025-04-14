package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.persistence.schema.BARCODE_LENGTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BarcodeScanManager {
    private var barcodeBuffer = ""

    val isListening = MutableStateFlow(true)
    private var lastScannedBarcode = MutableStateFlow("")
    val _lastScannedBarcode = lastScannedBarcode.asStateFlow()

    fun notify(character: Char) {
        if (barcodeBuffer.length == BARCODE_LENGTH) complete()
        barcodeBuffer += character
    }

    fun complete() {
        lastScannedBarcode.value = barcodeBuffer
        barcodeBuffer = ""
    }

    fun cleanBarcode() {
        lastScannedBarcode.value = ""
    }
}