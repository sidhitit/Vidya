package com.cf.vidya.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme

/** Logo + search pill + profile icon, as on the Home design. */
@Composable
fun VidyaTopBar(
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.s),
    ) {
        VidyaLogo()
        Spacer(Modifier.width(VidyaSpacing.m))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(MaterialTheme.shapes.small)
                .background(VidyaColors.Surface)
                .border(1.dp, VidyaColors.Outline, MaterialTheme.shapes.small)
                .clickable(onClick = onSearchClick),
        ) {
            Text(
                text = stringResource(R.string.search_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = VidyaColors.TextTertiary,
            )
        }
        Spacer(Modifier.width(VidyaSpacing.xs))
        IconButton(onClick = onProfileClick) {
            Icon(
                painter = painterResource(R.drawable.ic_account_circle),
                contentDescription = stringResource(R.string.profile),
                tint = VidyaColors.TextPrimary,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

/** Placeholder until the designer exports the real logo asset. */
@Composable
fun VidyaLogo(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .clip(MaterialTheme.shapes.small)
            .background(VidyaColors.LogoGradient),
    ) {
        Text(
            text = "V",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = VidyaColors.Gold,
        )
    }
}

@Preview
@Composable
private fun VidyaTopBarPreview() {
    VidyaTheme {
        VidyaTopBar(onSearchClick = {}, onProfileClick = {})
    }
}
