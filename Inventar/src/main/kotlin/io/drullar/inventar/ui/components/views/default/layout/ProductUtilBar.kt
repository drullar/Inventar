package io.drullar.inventar.ui.components.views.default.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.search.SearchBar
import io.drullar.inventar.ui.provider.getLayoutStyle
import io.drullar.inventar.ui.style.Colors
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.LayoutStyle

@Composable
fun ProductUtilBar(
    modifier: Modifier = Modifier,
    onNewProductButtonClick: () -> Unit,
    onSearch: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.padding(5.dp).fillMaxWidth(0.7f)
    ) {
        TextButton(
            text = getText("product.new"),
            onClick = onNewProductButtonClick,
            modifier = Modifier.padding(start = 10.dp),
            backgroundColor = Colors.DarkGreen,
            borderColor = Colors.DarkGreen
        )

        SearchBar(Modifier.fillMaxWidth(0.7f)) { onSearch(it) }
        if (getLayoutStyle() == LayoutStyle.NORMAL)
            Spacer(Modifier)
    }
}