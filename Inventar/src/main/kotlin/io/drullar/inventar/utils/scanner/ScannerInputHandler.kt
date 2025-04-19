package io.drullar.inventar.utils.scanner

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import io.drullar.inventar.ui.viewmodel.delegate.impl.BarcodeScanManagerInterface

object ScannerInputHandler {

    fun handleEvent(
        event: KeyEvent,
        barcodeScanManager: BarcodeScanManagerInterface
    ): Boolean {
        if (event.type == KeyEventType.KeyDown) {
            if (event.key == Key.Enter) {
                barcodeScanManager.complete()
            } else {
                val character = event.utf16CodePoint.toChar()
                if (isCharacterValidBarcodeCharacter(character)) {
                    barcodeScanManager.append(character)
                }
            }
            return true
        }
        return false
    }

    private fun isCharacterValidBarcodeCharacter(char: Char) = char.code in 33..122
}