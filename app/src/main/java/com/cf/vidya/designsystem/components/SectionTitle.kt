package com.cf.vidya.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaSpacing

/** Heading above a row of content, e.g. "Continue Watching". */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = VidyaColors.TextPrimary,
        modifier = modifier.padding(horizontal = VidyaSpacing.l, vertical = VidyaSpacing.s),
    )
}
