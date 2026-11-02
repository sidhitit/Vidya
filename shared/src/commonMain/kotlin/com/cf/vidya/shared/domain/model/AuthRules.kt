package com.cf.vidya.shared.domain.model

/** Input rules for phone login, shared by every screen that needs them. */
object AuthRules {
    const val MOBILE_LENGTH = 10
    const val OTP_LENGTH = 6
    const val RESEND_SECONDS = 30

    /** Indian mobile numbers are 10 digits and start with 6, 7, 8 or 9. */
    fun isValidMobile(number: String): Boolean =
        number.length == MOBILE_LENGTH && number.all(Char::isDigit) && number.first() in '6'..'9'

    fun isValidOtp(otp: String): Boolean = otp.length == OTP_LENGTH && otp.all(Char::isDigit)
}
