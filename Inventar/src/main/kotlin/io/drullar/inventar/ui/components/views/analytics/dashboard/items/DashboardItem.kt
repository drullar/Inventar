package io.drullar.inventar.ui.components.views.analytics.dashboard.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.appTypography

@Composable
fun DashboardItem(
    title: String,
    summary: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    Card(modifier, border = BorderStroke(1.dp, Color.Black)) {
        Column(Modifier.padding(start = 5.dp)) {
            Text(
                text = title,
                style = appTypography().titleLarge,
                fontWeight = FontWeight.W400,
                modifier = Modifier.wrapContentWidth()
            )
            if (summary != null)
                Text(
                    text = summary,
                    style = appTypography().bodySmall,
                    textAlign = TextAlign.Justify,
                    fontWeight = FontWeight.Thin
                )
        }

        content(Modifier)
    }
}