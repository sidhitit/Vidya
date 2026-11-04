package com.cf.vidya.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.cf.vidya.R
import kotlinx.serialization.Serializable

// Type-safe routes: a missing or wrong argument is a compile error, not a runtime crash.

@Serializable data object LoginDestination
@Serializable data class OtpDestination(val mobile: String)
@Serializable data object HomeDestination
@Serializable data object ExploreDestination
@Serializable data object LibraryDestination
@Serializable data object PremiumDestination
@Serializable data object ProfileDestination
@Serializable data object SettingsDestination
@Serializable data object ManageSubscriptionDestination
@Serializable data object SearchDestination
@Serializable data class SeriesDestination(val seriesId: String)

/** The four bottom-bar tabs. */
enum class TopLevelDestination(
    val route: Any,
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int,
    val highlighted: Boolean = false,
) {
    HOME(HomeDestination, R.string.tab_home, R.drawable.ic_home_outlined, R.drawable.ic_home_filled),
    EXPLORE(ExploreDestination, R.string.tab_explore, R.drawable.ic_explore, R.drawable.ic_explore),
    LIBRARY(LibraryDestination, R.string.tab_library, R.drawable.ic_bookmark_outlined, R.drawable.ic_bookmark_filled),
    PREMIUM(PremiumDestination, R.string.tab_premium, R.drawable.ic_crown, R.drawable.ic_crown, highlighted = true),
    ;

    fun matches(destination: NavDestination?): Boolean = when (this) {
        HOME -> destination?.hasRoute<HomeDestination>() == true
        EXPLORE -> destination?.hasRoute<ExploreDestination>() == true
        LIBRARY -> destination?.hasRoute<LibraryDestination>() == true
        PREMIUM -> destination?.hasRoute<PremiumDestination>() == true
    }
}
