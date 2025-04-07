package io.drullar.inventar.ui.components.views.analytics.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.ProductDTO
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.cards.ProductSalesCard
import io.drullar.inventar.ui.components.views.analytics.dashboard.items.DateSelector
import io.drullar.inventar.ui.viewmodel.AnalyticsViewModel
import java.time.LocalDate
import io.drullar.inventar.ui.components.search.SearchBar
import io.drullar.inventar.ui.components.views.analytics.dashboard.items.DashboardItem
import io.drullar.inventar.ui.components.window.dialog.ProductPickerDialog
import java.util.Locale

@Composable
fun PredefinedDashboard(viewModel: AnalyticsViewModel) {
    val locale = viewModel.getSettings().value.language.locale

    LazyVerticalGrid(columns = GridCells.Adaptive(600.dp)) {
        item { ProductSalesAnalytics(locale, viewModel, Modifier.height(300.dp)) }
    }
}

@Composable
private fun ProductSalesAnalytics(
    locale: Locale,
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    DashboardItem(
        title = "Product sales",
        summary = "Shows the amount of sales the selected product has for the provided time range",
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
                    descriptionText = "Starting date:",
                    locale = locale,
                ) { fromDate = it }

                DateSelector(
                    modifier = Modifier.fillMaxWidth(),
                    preselectedDate = untilDate,
                    descriptionText = "Until date:",
                    locale = locale
                ) { untilDate = it }
            }

            TextButton(
                text = "Select product",
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