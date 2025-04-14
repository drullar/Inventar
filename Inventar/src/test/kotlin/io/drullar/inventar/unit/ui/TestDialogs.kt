package io.drullar.inventar.unit.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilDoesNotExist
import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.window.dialog.AlertDialog
import io.drullar.inventar.ui.components.window.dialog.NewProductDialog
import io.drullar.inventar.ui.components.window.dialog.ChangeProductQuantityDialog
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.utils.ContentDescription
import io.drullar.inventar.unit.utils.Factory
import org.junit.Test
import java.math.BigDecimal

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
    fun orderProductConfirmationDialog() = runComposeUiTest {
        var submittedQuantity = 0
        setContent {
            ChangeProductQuantityDialog(
                product = Factory.createProduct(),
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
        inputField.fetchSemanticsNode()
    }

    @Test
    fun newProductDialog() = runComposeUiTest {
        var product: ProductDTO? = null
        setContent {
            var dialogIsVisible by remember { mutableStateOf(true) }
            if (dialogIsVisible)
                NewProductDialog(
                    onSubmit = {
                        product = ProductDTO(
                            uid = 0,
                            name = it.name,
                            sellingPrice = it.sellingPrice,
                            providerPrice = it.providerPrice,
                            barcode = it.barcode,
                            availableQuantity = it.availableQuantity
                        )
                        dialogIsVisible = false
                    },
                    onClose = { dialogIsVisible = false }
                )
        }

        val nameField =
            onNodeWithContentDescription(ContentDescription.NEW_PRODUCT_NAME)
        val sellingPriceField =
            onNodeWithContentDescription(ContentDescription.NEW_PRODUCT_SELLING)
        val barcodeField =
            onNodeWithContentDescription(ContentDescription.NEW_PRODUCT_BARCODE)
        val providerPriceField =
            onNodeWithContentDescription(ContentDescription.NEW_PRODUCT_PROVIDER)
        val quantityField =
            onNodeWithContentDescription(ContentDescription.NEW_PRODUCT_QUANTITY)

        nameField.assertIsDisplayed()
        sellingPriceField.assertIsDisplayed()
        barcodeField.assertIsDisplayed()
        providerPriceField.assertIsDisplayed()
        quantityField.assertIsDisplayed()

        val button = onNodeWithText(getText("label.save"))
        button.assertIsNotEnabled()
        button.performClick()
        sellingPriceField.assertIsDisplayed()
        barcodeField.assertIsDisplayed()
        providerPriceField.assertIsDisplayed()
        quantityField.assertIsDisplayed()

        sellingPriceField.performTextInput("2.0")
        nameField.performTextInput(" ")
        button.assertIsNotEnabled()
        val nameFieldWarning =
            onNodeWithText(getText("warning.validation.isEmpty"))
        val nameFieldWarningNode = nameFieldWarning.fetchSemanticsNode()
        nameFieldWarning.assertIsDisplayed()

        nameField.performTextClearance()
        nameField.performTextInput("ProductName")
        waitUntilDoesNotExist(SemanticsMatcher("", { it == nameFieldWarningNode }))
        onNodeWithText(getText("warning.validation.isEmpty"))
        quantityField.performTextInput("2")
        waitUntil(5000L) {
            !button.fetchSemanticsNode().config.contains(SemanticsProperties.Disabled)
        }
        button.assertIsEnabled()
        button.performClick()

        with(product) {
            assertAll {
                assertThat(this).isNotNull()
                assertThat(this!!.name).isEqualTo("ProductName")
                assertThat(this.availableQuantity).isEqualTo(2)
                assertThat(this.sellingPrice).isEqualTo(BigDecimal.valueOf(2.0))
            }
        }
    }
}