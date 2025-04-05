package io.drullar.inventar.ui.components.views.analytics.dashboard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DashboardItem(content: @Composable () -> Unit) {
    Card {
        content()
    }
}

@Preview
@Composable
fun DashboardItemPreview() {
    DashboardItem({
        Text("ASDFS")
    })
}