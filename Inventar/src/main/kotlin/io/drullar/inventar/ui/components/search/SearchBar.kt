package io.drullar.inventar.ui.components.search

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.utils.Icons

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearchSubmit: (String) -> Unit,
    contentOnSearch: (@Composable () -> Unit)? = null //TODO selectable
) { //TODO rename
    var searchQuery by remember { mutableStateOf("") }
    val fontSize = 14

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .border(1.dp, Color.Black, roundedBorderShape()),
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
                        text = "Search...",
                        color = Color.LightGray.copy(alpha = 0.5f),
                        fontSize = fontSize.sp
                    )
                }
                innerTextField()
            }
        )
        IconButton(onClick = { onSearchSubmit(searchQuery) }) {
            Icon(
                painterResource(Icons.SEARCH_ICON),
                contentDescription = "Search"
            )
        }
    }
}