package com.cf.vidya.shared.domain.model

data class UserProfile(
    /** Shown on the Profile card; users quote it to customer support. */
    val id: String,
    val isPremium: Boolean,
)
