package io.drullar.inventar.ui.components.button

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.NewWindowIcon

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onHoverText: String? = null,
    buttonColors: ButtonColors? = null,
    icon: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHoveredOn by interactionSource.collectIsHoveredAsState()

    Box(
        modifier.hoverable(interactionSource, onHoverText != null)
    ) {
        Button( //TODO button container/content colors
            onClick,
            colors = buttonColors ?: ButtonDefaults.buttonColors()
        ) {
            icon()
        }
        if (isHoveredOn && onHoverText != null) {
            Text(
                onHoverText,
                modifier = Modifier.background(Color(255, 255, 255, 120)),
                fontWeight = FontWeight.W500,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
@Preview
private fun NewWindowIconButton() {
    IconButton(onClick = {}, Modifier, "Open in external window") {
        NewWindowIcon(Modifier.size(20.dp))
    }
}