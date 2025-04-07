package io.drullar.inventar.ui.components.views.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.views.analytics.dashboard.PredefinedDashboard
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsView(analyticsViewModel: AnalyticsViewModel) {
    Column(Modifier.fillMaxWidth().fillMaxHeight().padding(horizontal = 10.dp)) {
        PredefinedDashboard(analyticsViewModel)
    }
}