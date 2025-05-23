package io.drullar.inventar.ui.components.search

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.provider.getText


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val fontSize = 14
    val interactionSource = remember { MutableInteractionSource() }
    val isSearchBarFocused = interactionSource.collectIsFocusedAsState()

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .border(1.dp, Color.Black, roundedBorderShape()).onKeyEvent {
                if (it.key == Key.Enter) onSearch(searchQuery)
                true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        BasicTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            textStyle = TextStyle(fontSize = fontSize.sp),
            singleLine = true,
            modifier = Modifier.weight(1f).padding(5.dp),
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = getText("label.search"),
                        color = Color.LightGray.copy(alpha = 0.5f),
                        fontSize = fontSize.sp
                    )
                }
                innerTextField() // has to be called as per the documentation
            },
            keyboardActions = KeyboardActions(onSearch = { onSearch(searchQuery) }),
            interactionSource = interactionSource
        )
        TextButton(
            text = getText("label.search"),
            onClick = { onSearch(searchQuery) },
            modifier = Modifier.padding(horizontal = 5.dp),
            focusable = isSearchBarFocused.value
        )
    }
}