package com.cf.vidya.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaTheme

/**
 * Single-line input with the gold outline from the login design. Turns red when [isError].
 *
 * The field owns its text in [state] (a [TextFieldState]) so typing never lags behind the ViewModel;
 * screens read changes from it and send them to the ViewModel as events.
 */
@Composable
fun VidyaTextField(
    state: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    inputTransformation: InputTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    val shape = MaterialTheme.shapes.medium
    BasicTextField(
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = inputTransformation,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = VidyaColors.TextPrimary),
        cursorBrush = SolidColor(VidyaColors.Gold),
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        modifier = modifier.fillMaxWidth().height(52.dp),
        // The decorator draws everything around the text: background, border, prefix, placeholder.
        decorator = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(VidyaColors.Surface.copy(alpha = 0.6f))
                    .border(1.dp, if (isError) VidyaColors.Error else VidyaColors.Gold, shape)
                    .padding(horizontal = 16.dp),
            ) {
                if (leading != null) {
                    leading()
                    Spacer(Modifier.width(12.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (state.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = VidyaColors.TextTertiary,
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

/** Keeps only digits, up to [maxLength]. Pasting "+91 98765 43210" keeps just the numbers. */
class DigitsOnly(private val maxLength: Int) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        val digits = asCharSequence().filter(Char::isDigit).take(maxLength)
        if (!asCharSequence().contentEquals(digits)) replace(0, length, digits)
    }
}

@Preview
@Composable
private fun VidyaTextFieldPreview() {
    VidyaTheme {
        VidyaTextField(state = rememberTextFieldState(), placeholder = "Enter OTP")
    }
}
