package com.cf.vidya.feature.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.DigitsOnly
import com.cf.vidya.designsystem.components.GoldButton
import com.cf.vidya.designsystem.components.VidyaTextField
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.AuthRules
import com.cf.vidya.shared.presentation.otp.OtpEvent
import com.cf.vidya.shared.presentation.otp.OtpState

private val GoldLink = TextLinkStyles(style = SpanStyle(color = VidyaColors.Gold))

@Composable
fun OtpScreen(
    state: OtpState,
    onEvent: (OtpEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The field keeps its own text; every change is reported to the ViewModel.
    val otpField = rememberTextFieldState(initialText = state.otp)
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(otpField) {
        snapshotFlow { otpField.text.toString() }.collect { currentOnEvent(OtpEvent.OtpChanged(it)) }
    }

    AuthLayout(modifier = modifier) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.otp_sent_to, state.mobile))
                append("\n")
                append(stringResource(R.string.otp_not_your_number))
                append(" ")
                // A tappable part of the text: goes back to Login with the number still filled in.
                withLink(LinkAnnotation.Clickable("edit", GoldLink) { onEvent(OtpEvent.EditNumberClicked) }) {
                    append(stringResource(R.string.otp_edit))
                }
            },
            style = MaterialTheme.typography.titleMedium,
            color = VidyaColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(VidyaSpacing.xl))

        val error = state.error
        VidyaTextField(
            state = otpField,
            placeholder = stringResource(R.string.otp_hint),
            isError = error != null,
            inputTransformation = DigitsOnly(AuthRules.OTP_LENGTH),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
            onKeyboardAction = { onEvent(OtpEvent.LoginClicked) },
            // Lets the keyboard suggest the code straight from the SMS.
            modifier = Modifier.semantics { contentType = ContentType.SmsOtpCode },
        )
        FieldError(error?.let { stringResource(it.messageRes()) })

        Spacer(Modifier.height(VidyaSpacing.l))
        GoldButton(
            text = stringResource(R.string.login),
            onClick = { onEvent(OtpEvent.LoginClicked) },
            enabled = state.isOtpComplete,
            loading = state.isVerifying,
        )
        Spacer(Modifier.height(VidyaSpacing.l))
        ResendText(state = state, onResend = { onEvent(OtpEvent.ResendClicked) })
    }
}

@Composable
private fun ResendText(state: OtpState, onResend: () -> Unit) {
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.otp_not_received))
            append(" ")
            when {
                state.resendSecondsLeft > 0 -> withStyle(SpanStyle(color = VidyaColors.TextPrimary)) {
                    append(stringResource(R.string.otp_resend_in, state.resendSecondsLeft))
                }
                state.isResending -> append(stringResource(R.string.otp_resending))
                else -> withLink(LinkAnnotation.Clickable("resend", GoldLink) { onResend() }) {
                    append(stringResource(R.string.otp_resend))
                }
            }
        },
        style = MaterialTheme.typography.bodySmall,
        color = VidyaColors.TextTertiary,
        textAlign = TextAlign.Center,
    )
}

// region Previews

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun OtpCountdownPreview() {
    VidyaTheme { OtpScreen(state = OtpState(mobile = "9650548592", resendSecondsLeft = 29), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun OtpWrongCodePreview() {
    VidyaTheme {
        OtpScreen(
            state = OtpState(mobile = "9650548592", otp = "111111", resendSecondsLeft = 0, error = AppError.InvalidOtp),
            onEvent = {},
        )
    }
}

// endregion
