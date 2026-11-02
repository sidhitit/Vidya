package com.cf.vidya.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.BottomBarItem
import com.cf.vidya.designsystem.components.PlaceholderScreen
import com.cf.vidya.designsystem.components.VidyaBottomBar
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.feature.auth.LoginRoute
import com.cf.vidya.feature.auth.OtpRoute
import com.cf.vidya.feature.home.HomeRoute
import com.cf.vidya.feature.library.LibraryRoute

/** App root: the navigation graph plus the floating bottom bar on tab screens. */
@Composable
fun VidyaApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val selectedTab = TopLevelDestination.entries.indexOfFirst { it.matches(currentDestination) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VidyaColors.Background),
    ) {
        NavHost(
            navController = navController,
            // TODO: start at Home when a saved session exists (needs local storage, backend phase).
            startDestination = LoginDestination,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<LoginDestination> {
                LoginRoute(onOpenOtp = { navController.navigate(OtpDestination(it)) })
            }
            composable<OtpDestination> { entry ->
                OtpRoute(
                    mobile = entry.toRoute<OtpDestination>().mobile,
                    onLoggedIn = {
                        navController.navigate(HomeDestination) {
                            // Remove Login and OTP so Back from Home closes the app instead of returning here.
                            popUpTo<LoginDestination> { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<HomeDestination> {
                HomeRoute(
                    onOpenSeries = { navController.navigate(SeriesDestination(it)) },
                    onOpenSearch = { navController.navigate(SearchDestination) },
                    onOpenProfile = { navController.navigate(ProfileDestination) },
                )
            }
            composable<ExploreDestination> { PlaceholderScreen(stringResource(R.string.tab_explore)) }
            composable<LibraryDestination> {
                LibraryRoute(
                    onOpenSeries = { navController.navigate(SeriesDestination(it)) },
                    onOpenExplore = { navController.navigateToTab(TopLevelDestination.EXPLORE) },
                )
            }
            composable<PremiumDestination> { PlaceholderScreen(stringResource(R.string.tab_premium)) }
            composable<ProfileDestination> { PlaceholderScreen(stringResource(R.string.profile)) }
            composable<SearchDestination> { PlaceholderScreen(stringResource(R.string.search)) }
            composable<SeriesDestination> { entry ->
                val route = entry.toRoute<SeriesDestination>()
                PlaceholderScreen("${stringResource(R.string.series)} ${route.seriesId}")
            }
        }

        if (selectedTab >= 0) {
            VidyaBottomBar(
                items = TopLevelDestination.entries.map {
                    BottomBarItem(stringResource(it.label), it.icon, it.selectedIcon, it.highlighted)
                },
                selectedIndex = selectedTab,
                onItemClick = { index -> navController.navigateToTab(TopLevelDestination.entries[index]) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = VidyaSpacing.m),
            )
        }
    }
}

private fun NavHostController.navigateToTab(tab: TopLevelDestination) {
    navigate(tab.route) {
        // Keep one copy of each tab and remember its scroll position.
        // Home is the root of the tabs (Login is removed from the back stack after sign-in).
        popUpTo<HomeDestination> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
