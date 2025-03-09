package io.drullar.inventar.ui.components.search

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
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
import io.drullar.inventar.ui.viewmodel.delegates.getText

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearchSubmit: (String) -> Unit,
    contentOnSearch: (@Composable () -> Unit)? = null //TODO dropdown of results to select from
) {
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
                        text = getText("label.search"),
                        color = Color.LightGray.copy(alpha = 0.5f),
                        fontSize = fontSize.sp
                    )
                }
                innerTextField() // has to be called as per the documentation
            }
        )
        IconButton(onClick = { onSearchSubmit(searchQuery) }) {
            Icon(
                painterResource(Icons.SEARCH),
                contentDescription = getText("label.search")
            )
        }
    }
}

@Preview
@Composable
private fun SearchbarPreview() {
    SearchBar(modifier = Modifier.height(50.dp), {})
}