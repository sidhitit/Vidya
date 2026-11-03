package com.cf.vidya.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.cf.vidya.R
import com.cf.vidya.designsystem.theme.VidyaColors
import com.cf.vidya.designsystem.theme.VidyaTheme
import kotlinx.coroutines.launch

data class BottomBarItem(
    val label: String,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int,
    /** The Premium tab is always gold, as in the design. */
    val highlighted: Boolean = false,
)

private val ItemWidth = 68.dp
private val ItemSpacing = 4.dp

// The edge in front moves quickly and the edge behind follows slowly,
// so the pill stretches while it travels and settles back like a drop of liquid.
private val LeadingEdgeSpring = spring<Float>(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium)
private val TrailingEdgeSpring = spring<Float>(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)

/** Floating pill-shaped bottom bar from the design, with a liquid-glass selection pill. */
@Composable
fun VidyaBottomBar(
    items: List<BottomBarItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(VidyaColors.Surface.copy(alpha = 0.92f))
            .border(1.dp, VidyaColors.Outline, CircleShape)
            .padding(6.dp),
    ) {
        if (selectedIndex >= 0) {
            LiquidIndicator(
                selectedIndex = selectedIndex,
                gold = items[selectedIndex].highlighted,
                modifier = Modifier.matchParentSize(),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(ItemSpacing),
            modifier = Modifier.selectableGroup(),
        ) {
            items.forEachIndexed { index, item ->
                BottomBarTab(
                    item = item,
                    selected = index == selectedIndex,
                    onClick = { onItemClick(index) },
                )
            }
        }
    }
}

@Composable
private fun LiquidIndicator(selectedIndex: Int, gold: Boolean, modifier: Modifier = Modifier) {
    val itemStep = (ItemWidth + ItemSpacing).value
    val targetLeft = selectedIndex * itemStep
    // Both edges are animated separately (in dp) so the pill can stretch.
    val left = remember { Animatable(targetLeft) }
    val right = remember { Animatable(targetLeft + ItemWidth.value) }

    LaunchedEffect(selectedIndex) {
        val movingRight = targetLeft > left.value
        launch { left.animateTo(targetLeft, if (movingRight) TrailingEdgeSpring else LeadingEdgeSpring) }
        launch {
            right.animateTo(targetLeft + ItemWidth.value, if (movingRight) LeadingEdgeSpring else TrailingEdgeSpring)
        }
    }

    val tint by animateColorAsState(if (gold) VidyaColors.Gold else Color.White, label = "indicatorTint")

    Box(
        modifier = modifier
            .layout { measurable, constraints ->
                val x = left.value.dp.roundToPx()
                val width = (right.value - left.value).dp.roundToPx().coerceAtLeast(0)
                val placeable = measurable.measure(Constraints.fixed(width, constraints.maxHeight))
                layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(x, 0) }
            }
            .graphicsLayer {
                // Gets a little flatter the more it is stretched.
                val stretch = (right.value - left.value) / ItemWidth.value - 1f
                scaleY = 1f - (stretch * 0.12f).coerceIn(0f, 0.15f)
            }
            .clip(CircleShape)
            // Frosted fill with a brighter top edge, like light hitting glass.
            .background(Brush.verticalGradient(listOf(tint.copy(alpha = 0.22f), tint.copy(alpha = 0.08f))))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(listOf(tint.copy(alpha = 0.45f), tint.copy(alpha = 0.08f))),
                shape = CircleShape,
            ),
    )
}

@Composable
private fun BottomBarTab(item: BottomBarItem, selected: Boolean, onClick: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    val color by animateColorAsState(
        targetValue = when {
            item.highlighted -> VidyaColors.Gold
            selected -> VidyaColors.TextPrimary
            else -> VidyaColors.TextSecondary
        },
        label = "tabColor",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "iconScale",
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(ItemWidth)
            .clip(CircleShape)
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = {
                    // A short, crisp click under the finger on every tab tap.
                    haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                    onClick()
                },
            )
            .padding(vertical = 6.dp),
    ) {
        Icon(
            painter = painterResource(if (selected) item.selectedIcon else item.icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(22.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
        )
        Text(text = item.label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Preview
@Composable
private fun VidyaBottomBarPreview() {
    VidyaTheme {
        VidyaBottomBar(
            items = listOf(
                BottomBarItem("Home", R.drawable.ic_home_outlined, R.drawable.ic_home_filled),
                BottomBarItem("Explore", R.drawable.ic_explore, R.drawable.ic_explore),
                BottomBarItem("Library", R.drawable.ic_bookmark_outlined, R.drawable.ic_bookmark_filled),
                BottomBarItem("Premium", R.drawable.ic_crown, R.drawable.ic_crown, highlighted = true),
            ),
            selectedIndex = 0,
            onItemClick = {},
        )
    }
}
