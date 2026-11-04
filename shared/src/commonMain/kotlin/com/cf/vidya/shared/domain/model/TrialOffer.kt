package com.cf.vidya.shared.domain.model

/** The Premium tab's trial: pay [trialPrice] for [trialDays], then [renewalPrice] every [renewalMonths]. */
data class TrialOffer(
    val id: String,
    /** Prices are in whole rupees. */
    val trialPrice: Int,
    /** Shown struck through above the trial price. */
    val originalPrice: Int,
    val trialDays: Int,
    val renewalPrice: Int,
    val renewalMonths: Int,
    /** For the "1000+ Learning Series" benefit. */
    val seriesCount: Int,
    /** Posters fanned out at the top of the screen. */
    val previewPosters: List<String>,
)
