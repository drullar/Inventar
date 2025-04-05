package io.drullar.inventar.ui.components.views.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.views.analytics.dashboard.PredefinedDashboard
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsView(analyticsViewModel: AnalyticsViewModel = AnalyticsViewModel()) {
    var displayedAnalytics by remember { mutableStateOf(AnalyticsDashboardType.PREDEFINED) }
    Column(Modifier.fillMaxWidth().fillMaxHeight()) {
        SwitchDashboardButton(displayedAnalytics) {
            displayedAnalytics = it
        }

        when (displayedAnalytics) {
            AnalyticsDashboardType.PREDEFINED -> {
                PredefinedDashboard(analyticsViewModel)
            }

            else -> {
                /*TODO*/
            }
        }
    }
}

@Composable
private fun SwitchDashboardButton(
    currentDashboard: AnalyticsDashboardType,
    onChange: (AnalyticsDashboardType) -> Unit
) {
    var analyticsDropDownIsExpanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Dashboard", style = appTypography().bodyLarge)
        Spacer(Modifier.padding(5.dp))
        TextButton(
            currentDashboard.name,
            onClick = { analyticsDropDownIsExpanded = !analyticsDropDownIsExpanded }) {
            DropdownMenu(
                expanded = analyticsDropDownIsExpanded,
                onDismissRequest = { analyticsDropDownIsExpanded = false }) {
                AnalyticsDashboardType.entries.forEach {
                    DropdownMenuItem(text = { Text(it.name) }, onClick = {
                        analyticsDropDownIsExpanded = false
                        onChange(it)
                    })
                }
            }
        }
    }
}