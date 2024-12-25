package io.drullar.inventar.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.roundedBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPreviewCard(
    productData: ProductDTO,
    onClickCallback: (ProductDTO) -> Unit,
    isSelected: Boolean = false,
    selectionIsAllowed: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHoveredOn by interactionSource.collectIsHoveredAsState()
    val isSelectedState by remember { mutableStateOf(isSelected) }

    val width = 160.dp
    val height = 180.dp

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
        TooltipBox(
            positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
            tooltip = { PlainTooltip { Text(productData.name) } },
            state = TooltipState(isHoveredOn)
        ) {
            Text(
                text = productData.name,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                    .hoverable(interactionSource, selectionIsAllowed),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Image(
            painter = painterResource(productData.iconPath),
            contentDescription = productData.name,
            Modifier.roundedBorder()
                .size(width / 2, height / 2)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = productData.sellingPrice.toString(),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}