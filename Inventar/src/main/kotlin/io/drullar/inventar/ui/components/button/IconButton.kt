package io.drullar.inventar.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.style.highlightedLabelSmall

@Composable
fun IconButton(
    onClick: () -> Unit,
    focusable: Boolean = false,
    modifier: Modifier = Modifier,
    onHoverText: String? = null,
    buttonColors: ButtonColors? = null,
    icon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHoveredOn by interactionSource.collectIsHoveredAsState()

    Box(
        modifier.hoverable(interactionSource, onHoverText != null).padding(5.dp),
    ) {
        Button( //TODO button container/content colors
            onClick,
            colors = buttonColors ?: ButtonColors(
                Color.White,
                Color.White,
                Color.LightGray,
                Color.LightGray
            ),
            interactionSource = interactionSource,
            modifier = Modifier.focusProperties {
                canFocus = focusable // Used to prevent the button from reacting on Key.Enter event
            }
        ) {
            icon()
        }
        if (isHoveredOn && onHoverText != null) {
            Text(
                onHoverText,
                modifier = Modifier.background(Colors.PlatinumGray.copy(alpha = 0.3f)).align(
                    Alignment.BottomEnd
                ),
                fontStyle = FontStyle.Italic,
                style = appTypography().highlightedLabelSmall
            )
        }
    }
}