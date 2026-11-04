package com.cf.vidya.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.BackTopBar
import com.cf.vidya.designsystem.components.MenuCard
import com.cf.vidya.designsystem.components.MenuItem
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.shared.presentation.settings.SettingsEvent
import com.cf.vidya.shared.presentation.settings.SettingsState

@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background)
            .statusBarsPadding(),
    ) {
        BackTopBar(title = stringResource(R.string.settings), onBack = { onEvent(SettingsEvent.BackClicked) })
        Column(
            verticalArrangement = Arrangement.spacedBy(VidyaSpacing.l),
            modifier = Modifier.padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.s),
        ) {
            MenuCard(
                items = listOf(
                    MenuItem(stringResource(R.string.share_the_app)) { onEvent(SettingsEvent.ShareAppClicked) },
                    MenuItem(stringResource(R.string.contact_us)) { onEvent(SettingsEvent.ContactUsClicked) },
                    MenuItem(stringResource(R.string.terms_and_conditions)) { onEvent(SettingsEvent.TermsClicked) },
                    MenuItem(stringResource(R.string.privacy_policy)) { onEvent(SettingsEvent.PrivacyPolicyClicked) },
                ),
            )
            LogOutButton(
                onClick = { onEvent(SettingsEvent.LogoutClicked) },
                modifier = Modifier.align(Alignment.End),
            )
        }
    }

    if (state.isLogoutDialogVisible) {
        LogOutDialog(
            isLoggingOut = state.isLoggingOut,
            onConfirm = { onEvent(SettingsEvent.LogoutConfirmed) },
            onDismiss = { onEvent(SettingsEvent.LogoutDismissed) },
        )
    }
}

/** Small outlined "⏻ Log Out" button at the right, as in the design. */
@Composable
private fun LogOutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.medium
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape)
            .background(VidyaColors.Surface)
            .border(1.dp, VidyaColors.Outline, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.m),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_power),
            contentDescription = null,
            tint = VidyaColors.TextPrimary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(VidyaSpacing.m))
        Text(
            text = stringResource(R.string.log_out),
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextPrimary,
        )
    }
}

/** Not in the design: asks before logging out, since logging back in needs an OTP. */
@Composable
private fun LogOutDialog(isLoggingOut: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { if (!isLoggingOut) onDismiss() },
        containerColor = VidyaColors.SurfaceElevated,
        title = { Text(stringResource(R.string.log_out_title), color = VidyaColors.TextPrimary) },
        text = { Text(stringResource(R.string.log_out_message), color = VidyaColors.TextSecondary) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isLoggingOut) {
                if (isLoggingOut) {
                    CircularProgressIndicator(color = VidyaColors.Gold, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Text(stringResource(R.string.log_out), color = VidyaColors.Gold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoggingOut) {
                Text(stringResource(R.string.cancel), color = VidyaColors.TextSecondary)
            }
        },
    )
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun SettingsPreview() {
    VidyaTheme {
        SettingsScreen(state = SettingsState(), onEvent = {})
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun SettingsLogOutDialogPreview() {
    VidyaTheme {
        SettingsScreen(state = SettingsState(isLogoutDialogVisible = true), onEvent = {})
    }
}
