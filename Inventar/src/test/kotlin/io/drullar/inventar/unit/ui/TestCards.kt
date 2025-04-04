package io.drullar.inventar.unit.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.rightClick
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.drullar.inventar.shared.OrderCreationDTO
import io.drullar.inventar.shared.OrderDTO
import io.drullar.inventar.shared.OrderStatus
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.OrderDetailCardRenderContext
import io.drullar.inventar.ui.components.cards.OrderDetailPreviewCard
import io.drullar.inventar.ui.components.cards.ProductSummarizedPreviewCard
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.unit.utils.Factory.createOrder
import io.drullar.inventar.unit.utils.Factory.createProductDTO
import org.junit.Test
import java.math.BigDecimal
import java.util.Currency

@OptIn(ExperimentalTestApi::class)
class TestCards : AbstractUiTest() {

    @Test
    fun productSummarizedPreviewCard() = runComposeUiTest {
        var selectedProduct: ProductDTO? = null
        var addedProductToOrder: ProductDTO? = null
        var isDeleted = false

        setContent {
            var isSelected by remember { mutableStateOf(false) }

            if (!isDeleted) {
                ProductSummarizedPreviewCard(
                    productData = createProductDTO(
                        name = "ProductName",
                        sellingPrice = BigDecimal.valueOf(2.0)
                    ),
                    onClickCallback = {
                        selectedProduct = it
                        isSelected = true
                    },
                    isSelected = isSelected,
                    selectionIsAllowed = true,
                    currency = Currency.getInstance("BGN"),
                    onDeleteRequest = { isDeleted = true },
                    onEditRequest = { /* Context menu can't be tested with current version of Compose multiplatform*/ },
                    onAddToOrderRequest = { addedProductToOrder = it },
                )
            }
        }

        val card =
            onNodeWithContentDescription("Summarized preview of ProductName contents").assertIsDisplayed()
        val productNameText = onNodeWithText("ProductName").assertIsDisplayed()
        val productImage = onNodeWithContentDescription("ProductName image").assertIsDisplayed()
        val sellingPriceText =
            onNodeWithContentDescription("ProductName selling price").assertIsDisplayed()

        card.performClick()
        card.assertIsFocused()
        assertThat(selectedProduct!!.name).isEqualTo("ProductName")
        assertThat(selectedProduct!!.sellingPrice).isEqualTo(BigDecimal.valueOf(2.0))

        val addToOrderButton =
            onNodeWithContentDescription("ProductName add to order button")
                .assertIsDisplayed()
                .assertHasClickAction()

        addToOrderButton.performClick()
        assertThat(addedProductToOrder!!.name).isEqualTo("ProductName")
        assertThat(addedProductToOrder!!.sellingPrice).isEqualTo(BigDecimal.valueOf(2.0))
    }

    @Test
    fun orderDetailPreviewCard() = runComposeUiTest {
        var assertionOrder: OrderDTO = createOrder()
        setContent {
            var order by remember { mutableStateOf(assertionOrder) }

            OrderDetailPreviewCard(
                order,
                onTerminate = {
                    order = order.copy(status = OrderStatus.TERMINATED)
                    assertionOrder = order
                },
                onComplete = {
                    order = order.copy(status = OrderStatus.COMPLETED)
                    assertionOrder
                },
                onProductValueChange = { p, q ->
                    val updatedMap = order.productToQuantity.toMutableMap().also { it[p] = q }
                    order = order.copy(productToQuantity = updatedMap)
                },
                onProductRemove = { p ->
                    val updatedMap = order.productToQuantity.toMutableMap().also { it.remove(p) }
                    order = order.copy(productToQuantity = updatedMap)
                },
                renderContext = OrderDetailCardRenderContext.PREVIEW,
                currency = Currency.getInstance("BGN")
            )
        }

        val card = onNodeWithContentDescription(
            OrderDetailCardRenderContext.PREVIEW.buildContentDescription()
        ).assertIsDisplayed()

        val completeButton =
            onNodeWithText(getText("label.complete"))
                .assertIsDisplayed()
                .assertIsEnabled()
                .assertHasClickAction()

        val terminateButton = onNodeWithText(getText("label.terminate"))
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()

        completeButton.performClick()
//        waitUntil(5000L) { TODO fix
//            completeButton.isNotDisplayed()
//        }
    }
}