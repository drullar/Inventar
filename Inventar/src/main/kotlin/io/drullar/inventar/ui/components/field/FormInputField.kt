package io.drullar.inventar.ui.components.field

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.drullar.inventar.isNumeric
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.style.highlightedLabelSmall
import io.drullar.inventar.ui.style.roundedBorder
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Composable
fun <T : Any> FormInputField(
    label: String?,
    defaultValue: String,
    inputType: KClass<T>,
    onValueChange: (value: String) -> Unit,
    warningMessage: String? = null,
    characterLimit: Int? = null,
    fieldSemanticDescription: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val isWarningVisible = !warningMessage.isNullOrEmpty()
    BasicTextField(
        value = defaultValue,
        textStyle = appTypography().bodySmall,
        onValueChange = { changedValue ->
            if (characterLimit != null && changedValue.length > characterLimit) return@BasicTextField
            // Check whether desired inputType is number and whether the inputed value is numeric. If it's empty string the change should be allowed.
            if (inputType.isSubclassOf(Number::class) && !isNumeric(changedValue) && changedValue.isNotBlank()) return@BasicTextField
            onValueChange(changedValue)
        },
        modifier = modifier.roundedBorder().heightIn(50.dp, 70.dp)
            .semantics { contentDescription = fieldSemanticDescription },
        keyboardOptions = KeyboardOptions(
            keyboardType = when (inputType) {
                Double::class, Float::class -> KeyboardType.Decimal
                Number::class -> KeyboardType.Number
                else -> KeyboardType.Text
            }
        ),
        decorationBox = { innerTextField ->
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                label?.let {
                    // Label describing the field
                    Text(
                        text = label,
                        style = appTypography().labelSmall
                    )
                }
                // Form input contents
                innerTextField()
                if (isWarningVisible && warningMessage != null) {
                    Text(
                        text = warningMessage,
                        color = Color.Red,
                        textDecoration = TextDecoration.Underline,
                        style = appTypography().highlightedLabelSmall
                    )
                }
            }
        }
    )
}