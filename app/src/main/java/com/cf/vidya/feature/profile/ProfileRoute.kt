package com.cf.vidya.feature.profile

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cf.vidya.R
import com.cf.vidya.shared.presentation.profile.ProfileEffect
import com.cf.vidya.shared.presentation.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    onOpenSeries: (String) -> Unit,
    onOpenPremium: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val back by rememberUpdatedState(onBack)
    val openSeries by rememberUpdatedState(onOpenSeries)
    val openPremium by rememberUpdatedState(onOpenPremium)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProfileEffect.GoBack -> back()
                is ProfileEffect.OpenSeries -> openSeries(effect.seriesId)
                ProfileEffect.OpenPremium -> openPremium()
                is ProfileEffect.CopyToClipboard -> {
                    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Vidya ID", effect.text)))
                    // Android 13+ shows its own "Copied" confirmation.
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        Toast.makeText(context, R.string.id_copied, Toast.LENGTH_SHORT).show()
                    }
                }
                ProfileEffect.OpenManageSubscription -> {
                    // Google Play's own page for cancelling or changing this app's subscription.
                    val url = "https://play.google.com/store/account/subscriptions?package=${context.packageName}"
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                    } catch (_: ActivityNotFoundException) {
                        // No Play Store or browser on the device; nothing we can open.
                    }
                }
            }
        }
    }

    ProfileScreen(state = state, onEvent = viewModel::onEvent)
}
