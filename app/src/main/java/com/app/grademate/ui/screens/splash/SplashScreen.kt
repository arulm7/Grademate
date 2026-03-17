package com.app.grademate.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.grademate.datastore.DataStoreManager
import com.app.grademate.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    dataStoreManager: DataStoreManager
) {
    val isFirstTime by dataStoreManager.isFirstTime().collectAsState(initial = null)
    
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(isFirstTime) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        
        delay(2000L)
        if (isFirstTime != null) {
            navController.popBackStack()
            if (isFirstTime == true) {
                navController.navigate(Screen.Setup.route)
            } else {
                navController.navigate(Screen.Home.route)
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "App Logo",
                tint = Color.White,
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "GradeMate",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(alpha.value)
            )
        }
    }
}
