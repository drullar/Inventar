package io.drullar.inventar.ui.components.views.analytics.dashboard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun PredefinedDashboard(analyticsViewModel: AnalyticsViewModel) {
    LineChart(
        data = remember {
            listOf(
                Line(
                    label = "Windows",
                    values = emptyList(),
                    color = SolidColor(Color.Yellow),
                    curvedEdges = true,
                    dotProperties = DotProperties(
                        enabled = true,
                        color = SolidColor(Color.White),
                        strokeWidth = 4.dp,
                        radius = 7.dp,
                        strokeColor = SolidColor(Color.Blue),
                    )
                ),
                Line(
                    label = "Linux",
                    values = emptyList(),
                    color = SolidColor(Color.Yellow),
                    curvedEdges = true,
                    dotProperties = DotProperties(
                        enabled = true,
                        color = SolidColor(Color.White),
                        strokeWidth = 4.dp,
                        radius = 7.dp,
                        strokeColor = SolidColor(Color.Blue),
                    )
                )
            )
        },
        curvedEdges = false
    )
}

@Preview
@Composable
fun PredefinedDashboardPreview() {
    PredefinedDashboard(AnalyticsViewModel())
}