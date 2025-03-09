package io.drullar.inventar.ui.components.cards

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.roundedBorder
import io.drullar.inventar.ui.utils.Icons
import io.drullar.inventar.ui.viewmodel.delegates.getText
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSummarizedPreviewCard(
    productData: ProductDTO,
    onClickCallback: (ProductDTO) -> Unit,
    isSelected: Boolean = false,
    selectionIsAllowed: Boolean = true,
    currency: Currency,
    onDeleteRequest: (ProductDTO) -> Unit,
    onEditRequest: (ProductDTO) -> Unit,
    onAddToOrderRequest: (ProductDTO) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHoveredOn by interactionSource.collectIsHoveredAsState()
    val isSelectedState by remember { mutableStateOf(isSelected) }

    val width = 300.dp
    val height = 180.dp

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
                color = if (isSelectedState) Colors.INDIGO else Colors.BABY_BLUE
            ),
            modifier = Modifier
                .size(width = width, height = height)
                .clickable {
                    if (selectionIsAllowed && !isSelected) {
                        onClickCallback(productData)
                    }
                }
        ) {
            Column(
                modifier = Modifier.padding(10.dp).fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(productData.iconPath),
                        contentDescription = productData.name,
                        Modifier.roundedBorder()
                            .size(width / 2, height / 2)
                    )
                    Column {
                        Text(text = productData.sellingPrice.toString())
                        Text(text = currency.symbol)
                    }
                    Spacer(Modifier)
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(productData.name) } },
                        state = TooltipState(isHoveredOn)
                    ) {
                        Text(
                            text = productData.name,
                            modifier = Modifier.padding(16.dp)
                                .hoverable(interactionSource, selectionIsAllowed),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    FilledTonalIconButton(
                        onClick = { onAddToOrderRequest(productData) },
                        colors = IconButtonDefaults.filledTonalIconButtonColors()
                            .copy(containerColor = Colors.Green)
                    ) {
                        Icon(painterResource(Icons.ADD), null)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ProductSummarizedPreviewCard(
        ProductDTO(2, "productName"),
        currency = Currency.getInstance("BG"),
        onDeleteRequest = {},
        onEditRequest = {},
        onAddToOrderRequest = {},
        onClickCallback = {})
}