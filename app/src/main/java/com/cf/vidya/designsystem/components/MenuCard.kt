package com.cf.vidya.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing
import com.cf.vidya.designsystem.theme.VidyaTheme

data class MenuItem(val label: String, val onClick: () -> Unit)

/** Rounded card of tappable rows with chevrons and thin dividers, as on Settings and Manage Subscription. */
@Composable
fun MenuCard(items: List<MenuItem>, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.medium
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(VidyaColors.Surface)
            .border(1.dp, VidyaColors.Outline, shape),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                // Inset like the design, so the lines don't touch the card edges.
                HorizontalDivider(color = VidyaColors.Outline, modifier = Modifier.padding(horizontal = VidyaSpacing.m))
            }
            MenuRow(item)
        }
    }
}

@Composable
private fun MenuRow(item: MenuItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = item.onClick)
            .padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.l),
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            color = VidyaColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = VidyaColors.TextPrimary,
        )
    }
}

@Preview
@Composable
private fun MenuCardPreview() {
    VidyaTheme {
        MenuCard(items = listOf(MenuItem("Share the app") {}, MenuItem("Contact Us") {}, MenuItem("Privacy Policy") {}))
    }
}
