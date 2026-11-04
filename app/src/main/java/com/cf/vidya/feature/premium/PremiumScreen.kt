package com.cf.vidya.feature.premium

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.cf.vidya.R
import com.cf.vidya.designsystem.components.ErrorState
import com.cf.vidya.designsystem.components.GoldButton
import com.cf.vidya.designsystem.components.SkeletonBox
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme
import com.cf.vidya.feature.common.messageRes
import com.cf.vidya.shared.domain.model.TrialOffer
import com.cf.vidya.shared.presentation.premium.PremiumEvent
import com.cf.vidya.shared.presentation.premium.PremiumState
import kotlin.math.abs

private val FanPosterWidth = 140.dp
private val FanPosterHeight = 186.dp

/** Gap between neighbouring poster centres in the fan, measured from the design. */
private val FanStep = 82.dp

@Composable
fun PremiumScreen(
    state: PremiumState,
    onEvent: (PremiumEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VidyaColors.Background)
            .statusBarsPadding(),
    ) {
        val error = state.error
        val offer = state.offer
        when {
            state.isLoading -> PremiumSkeleton()
            error != null -> ErrorState(
                message = stringResource(error.messageRes()),
                onRetry = { onEvent(PremiumEvent.RetryClicked) },
            )
            offer != null -> PremiumContent(
                offer = offer,
                isStartingTrial = state.isStartingTrial,
                onStartTrial = { onEvent(PremiumEvent.StartTrialClicked) },
            )
        }
    }
}

@Composable
private fun PremiumContent(offer: TrialOffer, isStartingTrial: Boolean, onStartTrial: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Only the offer scrolls (on short phones); the button stays pinned above the bottom bar.
        Box(modifier = Modifier.weight(1f)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = VidyaSpacing.xl),
            ) {
                PosterFan(posters = offer.previewPosters)
                Spacer(Modifier.height(VidyaSpacing.xl))
                GoldTitle(text = stringResource(R.string.start_trial_title))
                Spacer(Modifier.height(VidyaSpacing.xl))
                OfferCard(offer = offer, modifier = Modifier.padding(horizontal = VidyaSpacing.xl))
                Spacer(Modifier.height(VidyaSpacing.l))
                Text(
                    text = stringResource(R.string.trial_note, offer.renewalMonths),
                    style = MaterialTheme.typography.bodySmall,
                    color = VidyaColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = VidyaSpacing.xl),
                )
            }
            // Text scrolling under the button fades out instead of being cut off.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(VidyaSpacing.xl)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, VidyaColors.Background))),
            )
        }
        GoldButton(
            text = stringResource(R.string.start_trial),
            onClick = onStartTrial,
            loading = isStartingTrial,
            modifier = Modifier.padding(
                start = VidyaSpacing.l,
                end = VidyaSpacing.l,
                bottom = VidyaSpacing.bottomBarClearance,
            ),
        )
    }
}

/**
 * The posters at the top: the middle one in front, the rest smaller and darker behind it.
 * They slide out from behind the middle poster when the screen opens.
 */
@Composable
private fun PosterFan(posters: List<String>, modifier: Modifier = Modifier) {
    val spread = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        spread.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow))
    }
    val middle = posters.lastIndex / 2

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(FanPosterHeight),
    ) {
        posters.forEachIndexed { index, url ->
            val distance = index - middle
            val depth = abs(distance)
            // 1, 0.75, 0.56… each step back is a quarter smaller.
            val scale = 1f - 0.25f * depth + 0.06f * depth * (depth - 1)
            FanPoster(
                url = url,
                isFront = depth == 0,
                modifier = Modifier
                    .zIndex(-depth.toFloat())
                    .graphicsLayer {
                        translationX = (FanStep * distance).toPx() * spread.value
                        scaleX = scale
                        scaleY = scale
                    },
                // Posters further back sit deeper in the shadow.
                dim = (0.35f * depth).coerceAtMost(0.7f),
            )
        }
    }
}

@Composable
private fun FanPoster(url: String, isFront: Boolean, dim: Float, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier = modifier
            .size(FanPosterWidth, FanPosterHeight)
            // The front poster casts a soft gold glow, as if lit from behind.
            .shadow(
                elevation = if (isFront) 24.dp else 8.dp,
                shape = shape,
                spotColor = if (isFront) VidyaColors.Gold else Color.Black,
                ambientColor = if (isFront) VidyaColors.Gold else Color.Black,
            )
            .clip(shape)
            .background(VidyaColors.Surface)
            .border(if (isFront) 1.5.dp else 1.dp, VidyaColors.CardOutline, shape),
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        if (dim > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = dim)),
            )
        }
    }
}

/** "──── START YOUR TRIAL NOW ────" with gold lines that fade out towards the screen edges. */
@Composable
private fun GoldTitle(text: String, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        GoldLine(fadeTowardsStart = true, modifier = Modifier.weight(1f))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = VidyaColors.Gold,
            modifier = Modifier.padding(horizontal = VidyaSpacing.s),
        )
        GoldLine(fadeTowardsStart = false, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun GoldLine(fadeTowardsStart: Boolean, modifier: Modifier = Modifier) {
    val colors = listOf(Color.Transparent, VidyaColors.Gold)
    Box(
        modifier = modifier
            .height(1.5.dp)
            .background(Brush.horizontalGradient(if (fadeTowardsStart) colors else colors.reversed())),
    )
}

@Composable
private fun OfferCard(offer: TrialOffer, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.extraLarge
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(VidyaColors.SurfaceElevated)
            .border(1.dp, VidyaColors.Outline, shape)
            .padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.xl),
    ) {
        Text(
            text = stringResource(R.string.trial_original_price, offer.originalPrice),
            style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough),
            color = VidyaColors.TextTertiary,
        )
        Text(
            text = stringResource(R.string.price_rupees, offer.trialPrice),
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 48.sp, lineHeight = 56.sp),
            fontWeight = FontWeight.Bold,
            color = VidyaColors.Gold,
        )
        Spacer(Modifier.height(VidyaSpacing.m))
        Text(
            text = pluralStringResource(
                R.plurals.trial_terms,
                offer.trialDays,
                offer.trialDays,
                offer.renewalPrice,
                offer.renewalMonths,
            ),
            style = MaterialTheme.typography.bodySmall,
            color = VidyaColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(VidyaSpacing.l))
        Text(
            text = stringResource(R.string.get_these_benefits),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = VidyaColors.TextPrimary,
        )
        Spacer(Modifier.height(VidyaSpacing.m))
        // Left-aligned as a group, centred in the card, like the design.
        Column(verticalArrangement = Arrangement.spacedBy(VidyaSpacing.m)) {
            BenefitRow(stringResource(R.string.trial_benefit_series, offer.seriesCount))
            BenefitRow(stringResource(R.string.trial_benefit_ads_free))
            BenefitRow(stringResource(R.string.trial_benefit_hd))
        }
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_check_circle),
            contentDescription = null,
            tint = VidyaColors.Gold,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(VidyaSpacing.m))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = VidyaColors.TextPrimary)
    }
}

@Composable
private fun PremiumSkeleton() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = VidyaSpacing.xl),
    ) {
        SkeletonBox(modifier = Modifier.size(FanPosterWidth, FanPosterHeight))
        Spacer(Modifier.height(VidyaSpacing.xl))
        SkeletonBox(modifier = Modifier.size(width = 200.dp, height = 20.dp))
        Spacer(Modifier.height(VidyaSpacing.xl))
        SkeletonBox(
            modifier = Modifier
                .padding(horizontal = VidyaSpacing.xl)
                .fillMaxWidth()
                .height(260.dp),
        )
    }
}

// region Previews

private val previewOffer = TrialOffer(
    id = "trial",
    trialPrice = 1,
    originalPrice = 99,
    trialDays = 1,
    renewalPrice = 599,
    renewalMonths = 3,
    seriesCount = 1000,
    previewPosters = List(5) { "" },
)

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun PremiumPreview() {
    VidyaTheme {
        PremiumScreen(state = PremiumState(isLoading = false, offer = previewOffer), onEvent = {})
    }
}

@Preview(showBackground = true, heightDp = 844)
@Composable
private fun PremiumLoadingPreview() {
    VidyaTheme {
        PremiumScreen(state = PremiumState(), onEvent = {})
    }
}

// endregion
