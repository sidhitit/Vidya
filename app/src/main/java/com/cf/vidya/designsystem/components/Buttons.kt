package com.cf.vidya.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaTheme

/** Primary gold button (Get OTP, Login, Watch Now). Turns grey when disabled. */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = VidyaColors.Gold,
            contentColor = VidyaColors.OnGold,
            disabledContainerColor = VidyaColors.Disabled,
            disabledContentColor = VidyaColors.OnDisabled,
        ),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview
@Composable
private fun GoldButtonPreview() {
    VidyaTheme {
        GoldButton(text = "Get OTP", onClick = {})
    }
}

@Preview
@Composable
private fun GoldButtonDisabledPreview() {
    VidyaTheme {
        GoldButton(text = "Get OTP", onClick = {}, enabled = false)
    }
}
