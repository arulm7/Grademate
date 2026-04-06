package com.app.grademate.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.app.grademate.ui.components.FeatureCard

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val userName by viewModel.userName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
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
                color = Color.Gray
            )
        }

            Spacer(modifier = Modifier.height(32.dp))

            FeatureCard(
                title = "CGPA Calculator",
                icon = Icons.Default.Calculate,
                onClick = { navController.navigate(Screen.Cgpa.route) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            FeatureCard(
                title = "Attendance Tracker",
                icon = Icons.Default.Timer,
                onClick = { navController.navigate(Screen.Attendance.route) }
            )
    }
}
