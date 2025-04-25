package io.drullar.inventar.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.appTypography
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSummarizedPreviewCard(
    productData: ProductDTO,
    onClickCallback: (ProductDTO) -> Unit,
    locale: Locale,
    selectionIsAllowed: Boolean = true,
    currency: Currency,
    onDeleteRequest: (ProductDTO) -> Unit,
    onEditRequest: (ProductDTO) -> Unit,
    onAddToOrderRequest: (ProductDTO) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHoveredOn by interactionSource.collectIsHoveredAsState()
    val width = 300.dp
    val height = 150.dp

    ContextMenuArea(
        items = {
            listOf(
                ContextMenuItem("${getText("label.delete")} \"${productData.name}\"") {
                    onDeleteRequest(productData)
                },
                ContextMenuItem("${getText("label.edit")} \"${productData.name}\"") {
                    onEditRequest(productData)
                },
                ContextMenuItem(getText("product.add", productData.name)) {
                    onAddToOrderRequest(productData)
                }
            )
        }
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(
                width = 2.dp,
                color = Colors.INDIGO
            ),
            modifier = Modifier
                .size(width = width, height = height)
                .clickable {
                    onClickCallback(productData)
                }
                .semantics {
                    contentDescription = "Summarized preview of ${productData.name} contents"
                }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().semantics {
                        contentDescription = "Product name and id for ${productData.name}"
                    },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#${productData.uid}", style = appTypography().headlineMedium)
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(productData.name) } },
                        state = TooltipState(isHoveredOn),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = productData.name,
                            modifier = Modifier.padding(16.dp)
                                .hoverable(interactionSource, selectionIsAllowed),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = appTypography().bodyMedium,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth().padding(5.dp)
                ) {
                    Text(
                        text = productData.sellingPrice.toString() + currency.getSymbol(locale),
                        style = appTypography().bodyLarge,
                        modifier = Modifier.semantics {
                            contentDescription = "${productData.name} selling price"
                        }
                    )
                    FilledTonalIconButton(
                        onClick = {
                            onAddToOrderRequest(productData)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors()
                            .copy(containerColor = Colors.Green),
                    ) {
                        Icon(painterResource(Icons.ADD), "${productData.name} add to order button")
                    }
                }
            }
        }
    }
}