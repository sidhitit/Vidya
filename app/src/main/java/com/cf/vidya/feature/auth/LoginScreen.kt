package com.cf.vidya.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.DigitsOnly
import com.cf.vidya.designsystem.components.GoldButton
import com.cf.vidya.designsystem.components.VidyaTextField
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.domain.model.AuthRules
import com.cf.vidya.shared.presentation.login.LoginEvent
import com.cf.vidya.shared.presentation.login.LoginState

@Composable
fun LoginScreen(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The field keeps its own text; every change is reported to the ViewModel.
    val mobileField = rememberTextFieldState(initialText = state.mobile)
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(mobileField) {
        snapshotFlow { mobileField.text.toString() }.collect { currentOnEvent(LoginEvent.MobileChanged(it)) }
    }

    AuthLayout(modifier = modifier) {
        Text(
            text = stringResource(R.string.login_tagline),
            style = MaterialTheme.typography.titleMedium,
            color = VidyaColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(VidyaSpacing.xl))

        val error = state.error
        val errorText = when {
            state.showInvalidMobile -> stringResource(R.string.error_invalid_mobile)
            error != null -> stringResource(error.messageRes())
            else -> null
        }
        VidyaTextField(
            state = mobileField,
            placeholder = stringResource(R.string.mobile_hint),
            isError = errorText != null,
            inputTransformation = DigitsOnly(AuthRules.MOBILE_LENGTH),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
            onKeyboardAction = { onEvent(LoginEvent.GetOtpClicked) },
            leading = { CountryCode() },
            // Lets Android / Google offer the phone number saved on the device.
            modifier = Modifier.semantics { contentType = ContentType.PhoneNumber },
        )
        FieldError(errorText)

        Spacer(Modifier.height(VidyaSpacing.l))
        GoldButton(
            text = stringResource(R.string.get_otp),
            onClick = { onEvent(LoginEvent.GetOtpClicked) },
            enabled = state.isMobileValid,
            loading = state.isSending,
        )
        Spacer(Modifier.height(VidyaSpacing.l))
        LegalText()
    }
}

@Composable
private fun CountryCode() {
    Text(
        text = stringResource(R.string.country_code),
        style = MaterialTheme.typography.bodyLarge,
        color = VidyaColors.Gold,
    )
    Spacer(Modifier.width(VidyaSpacing.m))
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(24.dp)
            .background(VidyaColors.Outline),
    )
}

/** Red helper line under a field; takes no space when there's no error. */
@Composable
internal fun FieldError(text: String?) {
    if (text != null) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = VidyaColors.Error,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = VidyaSpacing.s, start = VidyaSpacing.xs),
        )
    }
}

@Composable
private fun LegalText() {
    // TODO: make Terms of Use / Privacy Policy tappable once we have their URLs.
    val link = SpanStyle(
        color = VidyaColors.TextPrimary,
        fontWeight = FontWeight.SemiBold,
        textDecoration = TextDecoration.Underline,
    )
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.legal_agree))
            append(" ")
            withStyle(link) { append(stringResource(R.string.terms_of_use)) }
            append(" ")
            append(stringResource(R.string.legal_and))
            append(" ")
            withStyle(link) { append(stringResource(R.string.privacy_policy)) }
            append("\n")
            append(stringResource(R.string.legal_safe))
        },
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 15.sp),
        color = VidyaColors.TextTertiary,
        textAlign = TextAlign.Center,
    )
}

// region Previews

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoginEmptyPreview() {
    VidyaTheme { LoginScreen(state = LoginState(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoginFilledPreview() {
    VidyaTheme { LoginScreen(state = LoginState(mobile = "9650548592"), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun LoginInvalidPreview() {
    VidyaTheme { LoginScreen(state = LoginState(mobile = "1234567890"), onEvent = {}) }
}

// endregion
