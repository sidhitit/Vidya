package com.cf.vidya.feature.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.cf.vidya.R

/** Addresses and pages outside the app. */
object AppLinks {
    // TODO: placeholders — replace with Vidya's real support address and legal pages before release.
    const val SUPPORT_EMAIL = "support@example.com"
    const val TERMS_URL = "https://example.com/terms"
    const val PRIVACY_URL = "https://example.com/privacy"

    fun playStore(packageName: String) = "https://play.google.com/store/apps/details?id=$packageName"

    /** Google Play's page for cancelling or changing this app's subscription. */
    fun playSubscriptions(packageName: String) =
        "https://play.google.com/store/account/subscriptions?package=$packageName"

    /** Google Play's list of the user's purchases, where subscription payments appear. */
    const val PLAY_ORDER_HISTORY = "https://play.google.com/store/account/orderhistory"
}

fun Context.openUrl(url: String) = startOrToast(Intent(Intent.ACTION_VIEW, url.toUri()))

fun Context.emailSupport() = startOrToast(
    Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(AppLinks.SUPPORT_EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
    },
)

fun Context.shareApp() {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message, AppLinks.playStore(packageName)))
    }
    startOrToast(Intent.createChooser(send, null))
}

private fun Context.startOrToast(intent: Intent) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, R.string.no_app_to_open, Toast.LENGTH_SHORT).show()
    }
}
