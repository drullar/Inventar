package io.drullar.inventar.ui.components.search

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
    contentOnSearch: (@Composable () -> Unit)? = null //TODO dropdown of results to select from
) {
    var searchQuery by remember { mutableStateOf("") }
//    var searchContext by remember { mutableStateOf(SearchContext.Products) }
//    var expandSearchContextDropdown by remember { mutableStateOf(false) }
    val fontSize = 14

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .border(1.dp, Color.Black, roundedBorderShape()),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Box(
//            modifier = modifier
//                .fillMaxHeight()
//                .wrapContentWidth()
//                .clickable(true, onClick = {
//                    expandSearchContextDropdown = !expandSearchContextDropdown
//                })
//        ) {
//            DropdownMenu(
//                expanded = expandSearchContextDropdown,
//                onDismissRequest = { expandSearchContextDropdown = false }
//            ) {
//                SearchContext.entries.forEach {
//                    DropdownMenuItem(
//                        text = { Text(it.contextName) },
//                        onClick = { searchContext = it })
//                }
//            }
//        }

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
                innerTextField() // has to be called as per the documentation
            }
        )
        IconButton(onClick = { onSearchSubmit(searchQuery) }) {
            Icon(
                painterResource(Icons.SEARCH),
                contentDescription = "Search"
            )
        }
    }
}

@Preview
@Composable
private fun SearchbarPreview() {
    SearchBar(modifier = Modifier.height(50.dp), {})
}