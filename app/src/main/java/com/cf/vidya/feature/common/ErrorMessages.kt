package com.cf.vidya.feature.common

import androidx.annotation.StringRes
import com.cf.vidya.R
import com.cf.vidya.shared.core.AppError

/** The message the user sees for each error type. */
@StringRes
fun AppError.messageRes(): Int = when (this) {
    AppError.Network -> R.string.error_network
    AppError.InvalidOtp -> R.string.error_invalid_otp
    else -> R.string.error_generic
}
