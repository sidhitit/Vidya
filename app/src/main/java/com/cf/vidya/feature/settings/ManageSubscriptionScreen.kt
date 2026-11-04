package com.cf.vidya.feature.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.BackTopBar
import com.cf.vidya.designsystem.components.MenuCard
import com.cf.vidya.designsystem.components.MenuItem
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.AppLinks
import com.cf.vidya.feature.common.emailSupport
import com.cf.vidya.feature.common.openUrl

private val SubscriptionNotes = listOf(
    R.string.subscription_note_manage,
    R.string.subscription_note_play,
    R.string.subscription_note_active,
)

/**
 * Every row opens something outside the app (subscriptions are billed by Google Play),
 * so this screen has no state and no ViewModel.
 */
@Composable
fun ManageSubscriptionRoute(onBack: () -> Unit) {
    val context = LocalContext.current
    ManageSubscriptionScreen(
        onBack = onBack,
        onCancelSubscription = { context.openUrl(AppLinks.playSubscriptions(context.packageName)) },
        onTransactionHistory = { context.openUrl(AppLinks.PLAY_ORDER_HISTORY) },
        onContactUs = { context.emailSupport() },
    )
}

@Composable
fun ManageSubscriptionScreen(
    onBack: () -> Unit,
    onCancelSubscription: () -> Unit,
    onTransactionHistory: () -> Unit,
    onContactUs: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background)
            .statusBarsPadding(),
    ) {
        BackTopBar(title = stringResource(R.string.manage_subscription), onBack = onBack)
        Column(
            verticalArrangement = Arrangement.spacedBy(VidyaSpacing.l),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.s),
        ) {
            MenuCard(
                items = listOf(
                    MenuItem(stringResource(R.string.cancel_subscription), onCancelSubscription),
                    MenuItem(stringResource(R.string.transaction_history), onTransactionHistory),
                    MenuItem(stringResource(R.string.contact_us), onContactUs),
                ),
            )
            NoteCard(notes = SubscriptionNotes)
        }
    }
}

@Composable
private fun NoteCard(notes: List<Int>) {
    val shape = MaterialTheme.shapes.medium
    Column(
        verticalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(VidyaColors.Surface)
            .border(1.dp, VidyaColors.Outline, shape)
            .padding(VidyaSpacing.l),
    ) {
        Text(
            text = stringResource(R.string.please_note),
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextPrimary,
        )
        notes.forEach { NoteLine(it) }
    }
}

/** "* text", with wrapped lines lining up after the asterisk as in the design. */
@Composable
private fun NoteLine(@StringRes text: Int) {
    Row {
        Text(text = "*", style = MaterialTheme.typography.bodySmall, color = VidyaColors.TextSecondary)
        Text(
            text = stringResource(text),
            style = MaterialTheme.typography.bodySmall,
            color = VidyaColors.TextSecondary,
            modifier = Modifier.padding(start = VidyaSpacing.xs),
        )
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun ManageSubscriptionPreview() {
    VidyaTheme {
        ManageSubscriptionScreen(onBack = {}, onCancelSubscription = {}, onTransactionHistory = {}, onContactUs = {})
    }
}
