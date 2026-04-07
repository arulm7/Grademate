package com.app.grademate.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grademate.ui.theme.BlueLight
import com.app.grademate.ui.theme.BlueSky

@Composable
fun FloatingBottomBar(
    selectedIndex: Int,
    pagerOffset: Float = selectedIndex.toFloat(),
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onDrag: (Float) -> Unit = {},
    onDragStopped: () -> Unit = {}
) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, 0),
        Triple("History", Icons.Default.History, 1),
        Triple("Profile", Icons.Default.Person, 2)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            var totalDrag by remember { mutableFloatStateOf(0f) }
            val draggableState = rememberDraggableState { delta ->
                onDrag(delta)
            }

            var itemBounds by remember { mutableStateOf(List(3) { Rect.Zero }) }

            if (itemBounds.none { it == Rect.Zero }) {
                val floatIndex = pagerOffset.coerceIn(0f, 2f)
                val lowerIndex = floatIndex.toInt()
                val upperIndex = (lowerIndex + 1).coerceAtMost(2)
                val fraction = floatIndex - lowerIndex

                val startLeft = itemBounds[lowerIndex].left
                val startRight = itemBounds[lowerIndex].right
                val endLeft = itemBounds[upperIndex].left
                val endRight = itemBounds[upperIndex].right

                val left = lerp(startLeft, endLeft, fraction)
                val right = lerp(startRight, endRight, fraction)
                val width = right - left
                
                val density = LocalDensity.current
                val verticalPadding = with(density) { 10.dp.toPx() }
                val iconHeight = with(density) { 24.dp.toPx() }
                val pillHeight = with(density) { 38.dp.toPx() } // Slightly larger than icon + some padding

                // Align pill vertically to be centered with the icon
                // Icon top relative to item is verticalPadding
                val iconTop = itemBounds[0].top + verticalPadding
                val topOffset = iconTop + (iconHeight - pillHeight) / 2

                Box(
                    modifier = Modifier
                        .offset { IntOffset(left.roundToInt(), topOffset.roundToInt()) }
                        .size(
                            width = with(density) { width.toDp() },
                            height = with(density) { pillHeight.toDp() }
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    BlueLight.copy(alpha = 0.2f),
                                    BlueSky.copy(alpha = 0.2f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        onDragStopped = {
                            onDragStopped()
                        }
                    ),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { (label, icon, index) ->
                    BottomNavItem(
                        icon = icon,
                        label = label,
                        isSelected = selectedIndex == index,
                        onClick = { onItemSelected(index) },
                        onPositioned = { rect ->
                            if (itemBounds[index] != rect) {
                                val newBounds = itemBounds.toMutableList()
                                newBounds[index] = rect
                                itemBounds = newBounds
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onPositioned: (Rect) -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        label = "scale"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .onGloballyPositioned { onPositioned(it.boundsInParent()) }
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) BlueSky else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}