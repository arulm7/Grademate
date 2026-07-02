package com.app.grademate.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.grademate.navigation.Screen
import com.app.grademate.ui.components.AppTopBarWrapper
import com.app.grademate.ui.components.CircularFeatureCard

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val userName by viewModel.userName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        val displayName = userName.ifBlank { "Student" }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment =  Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hello, $displayName!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "What would you like to track today?",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularFeatureCard(
                    title = "CGPA\nCalculator",
                    icon = Icons.Default.Calculate,
                    gradientColors = listOf(Color(0xFF4FACFE), Color(0xFF00C6FF)),
                    onClick = { navController.navigate(Screen.Cgpa.route) },
                    bobbingDelayMillis = 0
                )

                CircularFeatureCard(
                    title = "Attendance\nTracker",
                    icon = Icons.Default.Timer,
                    gradientColors = listOf(Color(0xFFAB22FF), Color(0xFF6B11FF)),
                    onClick = { navController.navigate(Screen.Attendance.route) },
                    bobbingDelayMillis = 300 // Offset the float phase for premium feel
                )
            }
    }
}
