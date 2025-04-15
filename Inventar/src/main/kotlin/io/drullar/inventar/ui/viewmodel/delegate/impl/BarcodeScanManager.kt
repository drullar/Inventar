package io.drullar.inventar.ui.viewmodel.delegate.impl

import io.drullar.inventar.persistence.schema.BARCODE_LENGTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface BarcodeScanManagerInterface {
    fun notify(character: Char)
    fun complete()
    fun cleanBarcode()
}

class BarcodeScanManager : BarcodeScanManagerInterface {
    private var barcodeBuffer = ""

    val isListening = MutableStateFlow(true)
    private var lastScannedBarcode = MutableStateFlow("")
    val _lastScannedBarcode = lastScannedBarcode.asStateFlow()

    override fun notify(character: Char) {
        if (barcodeBuffer.length == BARCODE_LENGTH) {
            complete()
            return
        }
        barcodeBuffer += character
    }

    override fun complete() {
        lastScannedBarcode.value = barcodeBuffer
        barcodeBuffer = ""
    }

    override fun cleanBarcode() {
        lastScannedBarcode.value = ""
    }
}