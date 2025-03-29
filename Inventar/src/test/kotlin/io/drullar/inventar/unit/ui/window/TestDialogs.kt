package io.drullar.inventar.unit.ui.window

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.drullar.inventar.ui.components.window.dialog.AlertDialog
import io.drullar.inventar.ui.components.window.dialog.OrderProductConfirmation
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.unit.ui.AbstractUiTest
import io.drullar.inventar.unit.utils.DTOFactory
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class TestDialogs : AbstractUiTest() {

    @Test
    fun alertDialog() = runComposeUiTest {
        var onCompleteValue: String? = null
        var onCancelValue: String? = null
        setContent {
            AlertDialog(
                "Some text",
                resolveButtonText = getText("label.continue"),
                cancelButtonText = getText("label.cancel"),
                onResolve = { onCompleteValue = "Complete invoked" },
                onCancel = { onCancelValue = "Cancel invoked" }
            )
        }

        onNodeWithText("Continue").performClick()
        assertThat(onCompleteValue).isEqualTo("Complete invoked")

        onNodeWithText("Cancel").performClick()
        assertThat(onCancelValue).isEqualTo("Cancel invoked")
    }

    @Test
    fun addProductToOrderDialog() = runComposeUiTest {
        var submittedQuantity = 0
        setContent {
            OrderProductConfirmation(
                product = DTOFactory.product(),
                initialQuantity = 1,
                onConfirm = { submittedQuantity = it },
                onCancel = {}
            )
        }

        onNodeWithContentDescription("Dialog that allows you to specify the amount of a given product to add to an order").assertIsDisplayed()
        onNodeWithText("Add \"Product1\" to an order?").assertIsDisplayed()

        val inputField =
            onNodeWithContentDescription("Test field to input quantity which is to be added to the order")
        inputField.performTextClearance()
        inputField.performTextInput("0")
        inputField.assertTextEquals("")
        inputField.performTextInput("2")
        inputField.assertTextEquals("2")

        onNodeWithText(getText("label.add")).performClick()
        assertThat(submittedQuantity).isEqualTo(2)
    }
}