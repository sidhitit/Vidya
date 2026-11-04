package com.cf.vidya.feature.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.BackTopBar
import com.cf.vidya.designsystem.components.ErrorState
import com.cf.vidya.designsystem.components.GoldButton
import com.cf.vidya.designsystem.components.PosterCard
import com.cf.vidya.designsystem.components.SectionTitle
import com.cf.vidya.designsystem.components.SkeletonBox
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.core.AppError
import com.cf.vidya.shared.domain.model.ContinueWatching
import com.cf.vidya.shared.domain.model.Series
import com.cf.vidya.shared.domain.model.UserProfile
import com.cf.vidya.shared.domain.usecase.ProfileOverview
import com.cf.vidya.shared.presentation.profile.ProfileEvent
import com.cf.vidya.shared.presentation.profile.ProfileState

private const val PosterAspectRatio = 3f / 4f
private val PosterWidth = 120.dp

private data class Benefit(@param:DrawableRes val icon: Int, @param:StringRes val label: Int)

private val PremiumBenefits = listOf(
    Benefit(R.drawable.ic_benefit_unlimited, R.string.benefit_unlimited),
    Benefit(R.drawable.ic_benefit_ads_free, R.string.benefit_ads_free),
    Benefit(R.drawable.ic_benefit_hd, R.string.benefit_hd),
    Benefit(R.drawable.ic_benefit_logins, R.string.benefit_logins),
)

@Composable
fun ProfileScreen(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background)
            .statusBarsPadding(),
    ) {
        BackTopBar(
            title = stringResource(R.string.profile),
            onBack = { onEvent(ProfileEvent.BackClicked) },
            actions = {
                IconButton(onClick = { onEvent(ProfileEvent.SettingsClicked) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.settings),
                        tint = VidyaColors.TextPrimary,
                    )
                }
            },
        )

        val error = state.error
        val overview = state.overview
        when {
            state.isLoading -> ProfileSkeleton()
            error != null -> ErrorState(
                message = stringResource(error.messageRes()),
                onRetry = { onEvent(ProfileEvent.RetryClicked) },
            )
            overview != null -> ProfileContent(overview = overview, onEvent = onEvent)
        }
    }
}

@Composable
private fun ProfileContent(overview: ProfileOverview, onEvent: (ProfileEvent) -> Unit) {
    val onSeriesClick: (String) -> Unit = { onEvent(ProfileEvent.SeriesClicked(it)) }
    LazyColumn(
        contentPadding = PaddingValues(top = VidyaSpacing.s, bottom = VidyaSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(VidyaSpacing.l),
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        item(key = "plan") {
            PlanCard(
                profile = overview.profile,
                onCopyId = { onEvent(ProfileEvent.CopyIdClicked) },
                onGetPremium = { onEvent(ProfileEvent.GetPremiumClicked) },
                modifier = Modifier.padding(horizontal = VidyaSpacing.l),
            )
        }
        if (overview.library.isNotEmpty()) {
            item(key = "library") {
                PosterSection(
                    title = stringResource(R.string.my_library),
                    posters = overview.library.map { it to null },
                    onSeriesClick = onSeriesClick,
                )
            }
        }
        if (overview.continueWatching.isNotEmpty()) {
            item(key = "continue_watching") {
                PosterSection(
                    title = stringResource(R.string.continue_watching),
                    posters = overview.continueWatching.map { it.series to it.progress },
                    onSeriesClick = onSeriesClick,
                )
            }
        }
        if (overview.profile.isPremium) {
            item(key = "manage_subscription") {
                ManageSubscriptionRow(
                    onClick = { onEvent(ProfileEvent.ManageSubscriptionClicked) },
                    modifier = Modifier.padding(horizontal = VidyaSpacing.l),
                )
            }
        }
    }
}

/** The dark-red card with the user ID, plan name and plan benefits. */
@Composable
private fun PlanCard(
    profile: UserProfile,
    onCopyId: () -> Unit,
    onGetPremium: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = MaterialTheme.shapes.large
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(VidyaColors.PremiumCard)
            .border(1.dp, VidyaColors.PremiumCardOutline, shape)
            .padding(VidyaSpacing.l),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserIdChip(id = profile.id, onClick = onCopyId)
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(if (profile.isPremium) R.string.premium_plan else R.string.free_plan),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (profile.isPremium) VidyaColors.Gold else VidyaColors.TextPrimary,
            )
        }
        Spacer(Modifier.height(VidyaSpacing.l))
        BenefitsDivider()
        Spacer(Modifier.height(VidyaSpacing.m))
        Row(modifier = Modifier.fillMaxWidth()) {
            PremiumBenefits.forEach { benefit ->
                BenefitItem(benefit = benefit, modifier = Modifier.weight(1f))
            }
        }
        // Not in the design yet: free users get a way to upgrade.
        if (!profile.isPremium) {
            Spacer(Modifier.height(VidyaSpacing.l))
            GoldButton(text = stringResource(R.string.get_premium), onClick = onGetPremium)
        }
    }
}

@Composable
private fun UserIdChip(id: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(onClickLabel = stringResource(R.string.copy_id), role = Role.Button, onClick = onClick)
            .padding(horizontal = VidyaSpacing.m, vertical = 6.dp),
    ) {
        Text(
            text = stringResource(R.string.user_id, id),
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextPrimary,
        )
        Spacer(Modifier.width(VidyaSpacing.s))
        Icon(
            painter = painterResource(R.drawable.ic_copy),
            contentDescription = null,
            tint = VidyaColors.TextPrimary,
            modifier = Modifier.size(16.dp),
        )
    }
}

/** "──── Premium Benefits ────" with gold lines that fade out at the ends. */
@Composable
private fun BenefitsDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, VidyaColors.Gold))),
        )
        Text(
            text = stringResource(R.string.premium_benefits),
            style = MaterialTheme.typography.bodySmall,
            color = VidyaColors.TextSecondary,
            modifier = Modifier.padding(horizontal = VidyaSpacing.s),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(VidyaColors.Gold, Color.Transparent))),
        )
    }
}

@Composable
private fun BenefitItem(benefit: Benefit, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Icon(
            painter = painterResource(benefit.icon),
            contentDescription = null,
            tint = VidyaColors.Gold,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.height(VidyaSpacing.xs))
        Text(
            text = stringResource(benefit.label),
            style = MaterialTheme.typography.bodySmall,
            color = VidyaColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/** A titled row of posters; each poster can carry a watch progress (null = no bar). */
@Composable
private fun PosterSection(
    title: String,
    posters: List<Pair<Series, Float?>>,
    onSeriesClick: (String) -> Unit,
) {
    Column {
        SectionTitle(text = title)
        LazyRow(
            contentPadding = PaddingValues(horizontal = VidyaSpacing.l),
            horizontalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
        ) {
            items(posters, key = { it.first.id }) { (item, progress) ->
                PosterCard(
                    posterUrl = item.posterUrl,
                    contentDescription = item.title,
                    viewCount = item.viewCount,
                    onClick = { onSeriesClick(item.id) },
                    progress = progress,
                    modifier = Modifier
                        .width(PosterWidth)
                        .aspectRatio(PosterAspectRatio),
                )
            }
        }
    }
}

@Composable
private fun ManageSubscriptionRow(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.medium
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, VidyaColors.Outline, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = VidyaSpacing.l, vertical = 14.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_crown),
            contentDescription = null,
            tint = VidyaColors.TextTertiary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(VidyaSpacing.m))
        Text(
            text = stringResource(R.string.manage_subscription),
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextSecondary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = VidyaColors.TextTertiary,
        )
    }
}

@Composable
private fun ProfileSkeleton() {
    Column(
        verticalArrangement = Arrangement.spacedBy(VidyaSpacing.l),
        modifier = Modifier.padding(top = VidyaSpacing.s),
    ) {
        SkeletonBox(
            modifier = Modifier
                .padding(horizontal = VidyaSpacing.l)
                .fillMaxWidth()
                .height(170.dp),
        )
        repeat(2) {
            Column(verticalArrangement = Arrangement.spacedBy(VidyaSpacing.m)) {
                SkeletonBox(
                    modifier = Modifier
                        .padding(horizontal = VidyaSpacing.l)
                        .size(width = 140.dp, height = 20.dp),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(VidyaSpacing.m),
                    modifier = Modifier.padding(horizontal = VidyaSpacing.l),
                ) {
                    repeat(3) {
                        SkeletonBox(modifier = Modifier.width(PosterWidth).aspectRatio(PosterAspectRatio))
                    }
                }
            }
        }
    }
}

// region Previews

private val previewSeries = List(4) { index ->
    Series(
        id = "s$index",
        title = "Redemption",
        posterUrl = "",
        viewCount = 750_000,
        totalEpisodes = 10,
        isPremium = false,
    )
}

private fun previewOverview(isPremium: Boolean) = ProfileOverview(
    profile = UserProfile(id = "241378600", isPremium = isPremium),
    library = previewSeries,
    continueWatching = previewSeries.map { ContinueWatching(it, lastEpisodeNumber = 3) },
)

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ProfilePremiumPreview() {
    VidyaTheme {
        ProfileScreen(state = ProfileState(isLoading = false, overview = previewOverview(true)), onEvent = {})
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ProfileFreePreview() {
    VidyaTheme {
        ProfileScreen(state = ProfileState(isLoading = false, overview = previewOverview(false)), onEvent = {})
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ProfileLoadingPreview() {
    VidyaTheme { ProfileScreen(state = ProfileState(), onEvent = {}) }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ProfileErrorPreview() {
    VidyaTheme {
        ProfileScreen(state = ProfileState(isLoading = false, error = AppError.Network), onEvent = {})
    }
}

// endregion
