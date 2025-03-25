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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.drullar.inventar.ui.style.appTypography
import io.drullar.inventar.ui.style.highlightedLabelSmall
import io.drullar.inventar.ui.style.roundedBorder
import kotlin.reflect.KClass

@Composable
fun <T : Any> FormInputField(
    label: String,
    defaultValue: String,
    inputType: KClass<T>,
    onValueChange: (value: String) -> Unit,
    warningMessage: String? = null,
    characterLimit: Int? = null
) {
    val isWarningVisible = !warningMessage.isNullOrEmpty()

    BasicTextField(
        value = defaultValue,
        textStyle = appTypography().bodySmall,
        onValueChange = { changedValue ->
            if (characterLimit != null && changedValue.length > characterLimit) return@BasicTextField
            onValueChange(changedValue)
        },
        modifier = Modifier.fillMaxWidth().roundedBorder().heightIn(50.dp, 70.dp),
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
                // Label describing the field
                Text(
                    text = label,
                    style = appTypography().labelSmall
                )
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