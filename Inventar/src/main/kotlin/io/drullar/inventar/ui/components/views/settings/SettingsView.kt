package io.drullar.inventar.ui.components.views.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.drullar.inventar.shared.OnScan
import io.drullar.inventar.shared.Settings
import io.drullar.inventar.shared.SupportedLanguage
import io.drullar.inventar.ui.components.button.TextButton
import io.drullar.inventar.ui.components.field.FormInputField
import io.drullar.inventar.ui.provider.getLayoutStyle
import io.drullar.inventar.ui.provider.getText
import io.drullar.inventar.ui.style.LayoutStyle
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.style.roundedBorderShape
import io.drullar.inventar.ui.viewmodel.SettingsViewModel
import java.util.Currency

@Composable
fun SettingsView(viewModel: SettingsViewModel) {
    val settings = viewModel.getSettings().collectAsState()
    var settingsCopy by remember { mutableStateOf(settings.value) }
    var languagesDropDownExpanded by remember { mutableStateOf(false) }
    var showCurrencyWarningMessage by remember { mutableStateOf(false) }
    var scanModeDropDownExpanded by remember { mutableStateOf(false) }

    var currencyInputField by remember { mutableStateOf(settingsCopy.defaultCurrency.currencyCode) }

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
                .border(1.dp, Color.Black, roundedBorderShape())
                .fillMaxWidth(if (getLayoutStyle() == LayoutStyle.COMPACT) 0.6f else 0.3f)
                .fillMaxHeight(0.8f)
                .padding(5.dp)
        ) {
            SettingsRow(getText("label.language")) {
                TextButton(
                    text = settingsCopy.language.localizedName.value,
                    onClick = { languagesDropDownExpanded = !languagesDropDownExpanded },
                    modifier = Modifier.width(200.dp)
                ) {
                    DropdownMenu(
                        expanded = languagesDropDownExpanded,
                        onDismissRequest = { languagesDropDownExpanded = false }
                    ) {
                        SupportedLanguage.entries.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language.localizedName.value) },
                                onClick = {
                                    settingsCopy = settingsCopy.copy(language = language)
                                    languagesDropDownExpanded = false
                                })
                        }
                    }
                }
            }

            SettingsRow(getText("label.currency")) {
                FormInputField(
                    label = null,
                    defaultValue = currencyInputField,
                    onValueChange = { input ->
                        if (input.length == 3) {
                            val isInputValid = validateCurrencyFormat(input)
                            showCurrencyWarningMessage = !isInputValid
                            if (isInputValid) settingsCopy =
                                settingsCopy.copy(defaultCurrency = Currency.getInstance(input))
                        }
                        currencyInputField = input
                    },
                    characterLimit = 3,
                    inputType = String::class,
                    fieldSemanticDescription = "Currency settings field",
                    warningMessage =
                    if (showCurrencyWarningMessage)
                        buildCurrencyWarningMessage()
                    else null,
                    modifier = Modifier.wrapContentWidth()
                )
            }

            SettingsRow(getText("label.scanner.mode")) {
                TextButton(
                    text = getText(settingsCopy.onScan.textProperty),
                    onClick = { scanModeDropDownExpanded = !scanModeDropDownExpanded }) {
                    DropdownMenu(
                        expanded = scanModeDropDownExpanded,
                        onDismissRequest = { scanModeDropDownExpanded = false }
                    ) {
                        OnScan.entries.forEach { scanMode ->
                            DropdownMenuItem(
                                text = { Text(getText(scanMode.textProperty)) },
                                onClick = {
                                    settingsCopy = settingsCopy.copy(onScan = scanMode)
                                    scanModeDropDownExpanded = false
                                })
                        }
                    }
                }
            }
        }
        if (hasChange(settings.value, settingsCopy) && !showCurrencyWarningMessage)
            TextButton(
                text = getText("label.save"),
                onClick = { viewModel.saveSettings(settingsCopy) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )
    }
}

private fun hasChange(settings: Settings, settingsCopy: Settings): Boolean {
    return settings != settingsCopy
}

private fun buildCurrencyWarningMessage() =
    getText("warning.currency.format") + " " + getText("warning.currency.format.url")

private fun validateCurrencyFormat(currencyCode: String): Boolean {
    try {
        Currency.getInstance(currencyCode)
        return true
    } catch (e: IllegalArgumentException) {
        return false
    }
}

@Composable
private fun SettingsRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.semantics { contentDescription = "$label settings" }.fillMaxWidth()
    ) {
        Text(label, style = appTypography().titleLarge, modifier = Modifier.fillMaxWidth(0.3f))
        content()
    }
}