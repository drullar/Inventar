package io.drullar.inventar.unit.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertThat
import assertk.assertions.isEqualTo
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.cards.ProductSummarizedPreviewCard
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
}