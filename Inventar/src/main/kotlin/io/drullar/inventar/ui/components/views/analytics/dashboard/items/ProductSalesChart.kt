package io.drullar.inventar.ui.components.views.analytics.dashboard.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.DropdownMenu
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.window.dialog.ProductPickerDialog
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.title.TextTitle
import org.jfree.data.category.DefaultCategoryDataset
import java.time.LocalDate
import java.util.Locale

@Composable
fun ProductSalesChart(chartSize: DpSize, locale: Locale, viewModel: AnalyticsViewModel) {
    DashboardItem(
        title = "Product overtime sales"
    ) {
        var showDropDownMenu by remember { mutableStateOf(false) }
        var currentPeriodSelection by remember { mutableStateOf(Period.DAYS) }
        var timeValue by remember { mutableStateOf(7) }
        var showProductsDialog by remember { mutableStateOf(false) }
        var targetProduct by remember { mutableStateOf<ProductDTO?>(null) }

        var chartTitle by remember { mutableStateOf<String?>(targetProduct?.let { "'${targetProduct!!.name}' sales" }) }
        val dataset = DefaultCategoryDataset().apply {
            if (targetProduct != null)
                setDataSet(this, timeValue, currentPeriodSelection, targetProduct!!, viewModel)
        }
        val chartXAxisText by remember {
            mutableStateOf(
                calculateXAxisText(
                    timeValue,
                    currentPeriodSelection
                )
            )
        }

        LaunchedEffect(targetProduct) {
            if (targetProduct != null) chartTitle = "${targetProduct!!.name} sales"
        }

        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
                .padding(start = 10.dp, bottom = 5.dp, end = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopStart).padding(10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    timeValue.toString(),
                    onValueChange = { change ->
                        change.toIntOrNull()?.let { timeValue = it }
                    },
                    textStyle = appTypography().bodyMedium.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.widthIn(75.dp, 110.dp).padding(end = 10.dp)
                )
                TextButton(
                    text = currentPeriodSelection.name,
                    onClick = {
                        showDropDownMenu = !showDropDownMenu
                    },
                ) {
                    DropdownMenu(
                        expanded = showDropDownMenu,
                        onDismissRequest = { showDropDownMenu = false }) {
                        Period.entries.forEach {
                            DropdownMenuItem(
                                text = { Text(it.name) },
                                onClick = { currentPeriodSelection = it })
                        }
                    }
                }
            }
            TextButton(
                text = "Select product",
                onClick = { showProductsDialog = true },
                modifier = Modifier.padding(top = 30.dp).align(Alignment.BottomStart)
            )
            SwingPanel(
                modifier = Modifier.size(chartSize).align(Alignment.BottomEnd),
                update = {
                    with(it.chart) {
                        chartTitle?.let { title = TextTitle(chartTitle) }
                        categoryPlot.domainAxis.label =
                            calculateXAxisText(timeValue, currentPeriodSelection)
                        categoryPlot.dataset = dataset

                    }

                },
                factory = {
                    ChartPanel(
                        ChartFactory.createLineChart(
                            chartTitle,
                            chartXAxisText,
                            "Sales",
                            dataset
                        )
                    )
                }
            )
        }

        if (showProductsDialog) {
            ProductPickerDialog(
                onDismissRequest = { showProductsDialog = false },
                onProductSelect = { product ->
                    showProductsDialog = false
                    targetProduct = product
                },
                analyticsViewModel = viewModel
            )
        }
    }
}

private fun calculateXAxisText(timeValue: Int, period: Period): String {
    return if (period == Period.MONTHS || period == Period.YEAR && timeValue == 1) "Month"
    else if (period == Period.YEAR) "Year"
    else "Day"
}

private fun setDataSet(
    dataSet: DefaultCategoryDataset,
    timeValue: Int,
    period: Period,
    product: ProductDTO,
    viewModel: AnalyticsViewModel
) {
    dataSet.clear()
    val today = LocalDate.now()
    when (period) {
        Period.YEAR -> today.minusYears(timeValue.toLong())
        Period.MONTHS -> today.minusMonths(timeValue.toLong())
        Period.DAYS -> today.minusDays(timeValue.toLong())
    }
    repeat(timeValue) { iteration ->
        val subtrahend = (timeValue - iteration).toLong()
        val untilDate: LocalDate
        val startDate: LocalDate
        when (period) {
            Period.YEAR -> {
                startDate = today.minusYears(subtrahend)
                untilDate = startDate.plusYears(1)
            }

            Period.MONTHS -> {
                startDate = today.minusMonths(subtrahend)
                untilDate = startDate.plusMonths(1)
            }

            Period.DAYS -> {
                startDate = today.minusDays(subtrahend)
                untilDate = startDate.plusDays(1)
            }
        }

        val sale = viewModel.getSalesForProduct(product, startDate, untilDate)
        dataSet.addValue(
            sale.soldQuantity,
            "Sales",
            when (period) {
                Period.YEAR -> {
                    untilDate.year
                }

                Period.DAYS -> {
                    "${untilDate.dayOfMonth} ${untilDate.month}"
                }

                Period.MONTHS -> {
                    untilDate.month
                }
            }
        )
    }
}

enum class Period {
    YEAR,
    MONTHS,
    DAYS
}