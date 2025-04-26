package io.drullar.inventar.ui.components.views.analytics.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.cards.ProductSalesCard
import io.drullar.inventar.ui.components.views.analytics.dashboard.items.DateSelector
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import java.time.LocalDate
import io.drullar.inventar.ui.components.views.analytics.dashboard.items.DashboardItem
import io.drullar.inventar.ui.components.views.analytics.dashboard.items.ProductSalesChart
import io.drullar.inventar.ui.components.window.external.ProductPickerDialog
import io.drullar.inventar.ui.provider.getText
import java.util.Locale

@Composable
fun PredefinedDashboard(viewModel: AnalyticsViewModel) {
    val locale = viewModel.getSettings().value.language.locale
    val scrollState = rememberLazyGridState()
    Box {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(700.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.align(Alignment.TopStart),
            state = scrollState
        ) {
            item {
                ProductSalesAnalytics(locale, viewModel, Modifier.height(300.dp))
            }
            item {
                ProductSalesChart(
                    DpSize(600.dp, 400.dp),
                    viewModel
                )
            }
        }
    }
}

@Composable
private fun ProductSalesAnalytics(
    locale: Locale,
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    DashboardItem(
        title = getText("label.product.sales"),
        summary = getText("label.product.sales.description"),
        modifier = modifier
    ) { contentModifier ->
        var fromDate by remember { mutableStateOf(LocalDate.EPOCH) }
        var untilDate by remember { mutableStateOf(LocalDate.now()) }
        var showProductsDialog by remember { mutableStateOf(false) }
        var targetProduct by remember { mutableStateOf<ProductDTO?>(null) }

        Box(contentModifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.TopStart)) {
                DateSelector(
                    modifier = Modifier.fillMaxWidth(),
                    preselectedDate = fromDate,
                    descriptionText = getText("label.starting.date"),
                    locale = locale,
                ) { fromDate = it }

                DateSelector(
                    modifier = Modifier.fillMaxWidth(),
                    preselectedDate = untilDate,
                    descriptionText = getText("label.until.date"),
                    locale = locale
                ) { untilDate = it }
            }

            TextButton(
                text = getText("label.select.product"),
                onClick = { showProductsDialog = true },
                modifier = Modifier.align(Alignment.CenterStart).padding(top = 30.dp)
            )

            if (targetProduct != null) {
                val productSales = viewModel.getSalesForProduct(
                    targetProduct!!,
                    fromDate,
                    untilDate
                ).soldQuantity

                ProductSalesCard(
                    product = targetProduct!!,
                    soldQuantity = productSales,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(10.dp)
                )
            }
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