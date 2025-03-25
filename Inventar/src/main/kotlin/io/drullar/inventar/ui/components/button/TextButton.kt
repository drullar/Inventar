package io.drullar.inventar.ui.components.button

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.style.highlightedLabelLarge
import androidx.compose.material3.Button as MaterialButton

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color = Colors.BrightBlue,
    textColor: Color = Color.White,
    borderColor: Color? = null,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null
) {
    MaterialButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors().copy(containerColor = backgroundColor),
        border = borderColor?.let { color -> BorderStroke(1.dp, color) },
        shape = RoundedCornerShape(5.dp),
        modifier = modifier
    ) {
        Text(
            text,
            color = textColor,
            style = appTypography().highlightedLabelLarge
        )
        content?.let { content -> content() }
    }
}

@Composable
@Preview
private fun DefaultButtonPreview() {
    TextButton("Complete", {})
}

@Composable
@Preview
private fun AltButtonPreview() {
    TextButton(
        "Terminate",
        {},
        backgroundColor = Color.White,
        textColor = Color.Red,
        borderColor = Color.Red
    )
}
