package io.drullar.inventar.ui.components.window.external

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.OrderDetailCardRenderContext
import io.drullar.inventar.ui.components.cards.OrderCreationCard
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.delegate.impl.BarcodeScanManagerInterface
import io.drullar.inventar.utils.scanner.ScannerInputHandler.handleEvent
import java.util.Currency
import java.util.Locale

@Composable
fun OrderCreationWindow(
    orderDTO: OrderDTO,
    onClose: () -> Unit,
    onTerminate: (OrderDTO) -> Unit,
    onComplete: (Boolean, OrderDTO) -> Unit,
    onProductValueChange: (ProductDTO, Int) -> Unit,
    onProductRemove: (ProductDTO, OrderDTO) -> Unit,
    currency: Currency,
    locale: Locale,
    barcodeScanManager: BarcodeScanManagerInterface,
    validateProductAvailability: (OrderDTO) -> Boolean
) {
    Window(
        title = "${getText("label.order")} #${orderDTO.orderId} preview",
        resizable = false,
        onCloseRequest = { onClose() },
        icon = painterResource(Icons.APP_ICON),
        state = rememberWindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition.Aligned(Alignment.CenterEnd),
            size = DpSize(500.dp, 500.dp)
        ),
        onKeyEvent = { event -> handleEvent(event, barcodeScanManager) },
        alwaysOnTop = false,
    ) {
        OrderCreationCard(
            order = orderDTO,
            onComplete = { onComplete(validateProductAvailability(orderDTO), orderDTO) },
            onTerminate = { onTerminate(orderDTO) },
            onProductValueChange = onProductValueChange,
            onProductRemove = { product -> onProductRemove(product, orderDTO) },
            renderContext = OrderDetailCardRenderContext.EXTERNAL_WINDOW,
            currency = currency,
            locale = locale
        )
    }
}