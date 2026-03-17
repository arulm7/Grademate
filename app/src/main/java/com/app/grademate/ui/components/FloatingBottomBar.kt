package com.app.grademate.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grademate.navigation.Screen
import com.app.grademate.ui.theme.BlueLight
import com.app.grademate.ui.theme.BlueSky

@Composable
fun FloatingBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple(Screen.Home.route, "Home", Icons.Default.Home),
        Triple(Screen.History.route, "History", Icons.Default.History),
        Triple(Screen.Profile.route, "Profile", Icons.Default.Person)
    )

    Row(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.LightGray)
            .background(Color.White, shape = RoundedCornerShape(32.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (route, label, icon) ->
            val isSelected = currentRoute == route
            BottomNavItem(
                icon = icon,
                label = label,
                isSelected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onNavigate(route)
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1f, label = "icon_scale")
    
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .background(
                brush = if (isSelected) {
                    Brush.linearGradient(listOf(BlueLight, BlueSky))
                } else {
                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                },
                shape = CircleShape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color.Gray,
                modifier = Modifier.size(24.dp).scale(scale)
            )
            
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = label,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
