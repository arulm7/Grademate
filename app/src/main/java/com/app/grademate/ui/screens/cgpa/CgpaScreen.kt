package com.app.grademate.ui.screens.cgpa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.grademate.ui.components.AppTopBarWrapper
import com.app.grademate.ui.components.GradientButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaScreen(
    navController: NavController,
    viewModel: CgpaViewModel
) {
    val gradeS by viewModel.gradeS.collectAsState()
    val gradeA by viewModel.gradeA.collectAsState()
    val gradeB by viewModel.gradeB.collectAsState()
    val gradeC by viewModel.gradeC.collectAsState()
    val gradeD by viewModel.gradeD.collectAsState()
    val gradeE by viewModel.gradeE.collectAsState()

    val totalSubjects by viewModel.totalSubjects.collectAsState()
    val calculatedCgpa by viewModel.calculatedCgpa.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lastCgpa by viewModel.lastCgpa.collectAsState()

    Scaffold(
        topBar = {
            AppTopBarWrapper(
                title = "CGPA Calculator",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last Calculated CGPA: " + String.format("%.2f", lastCgpa),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Total Subjects: $totalSubjects",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
            }

            // Grade Counters
            GradeCounterRow(grade = "S", point = 10, count = gradeS, onIncrement = { viewModel.updateGradeCount("S", 1) }, onDecrement = { viewModel.updateGradeCount("S", -1) })
            GradeCounterRow(grade = "A", point = 9, count = gradeA, onIncrement = { viewModel.updateGradeCount("A", 1) }, onDecrement = { viewModel.updateGradeCount("A", -1) })
            GradeCounterRow(grade = "B", point = 8, count = gradeB, onIncrement = { viewModel.updateGradeCount("B", 1) }, onDecrement = { viewModel.updateGradeCount("B", -1) })
            GradeCounterRow(grade = "C", point = 7, count = gradeC, onIncrement = { viewModel.updateGradeCount("C", 1) }, onDecrement = { viewModel.updateGradeCount("C", -1) })
            GradeCounterRow(grade = "D", point = 6, count = gradeD, onIncrement = { viewModel.updateGradeCount("D", 1) }, onDecrement = { viewModel.updateGradeCount("D", -1) })
            GradeCounterRow(grade = "E", point = 5, count = gradeE, onIncrement = { viewModel.updateGradeCount("E", 1) }, onDecrement = { viewModel.updateGradeCount("E", -1) })


            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.weight(0.3f).height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Reset")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                GradientButton(
                    text = "Calculate CGPA",
                    onClick = { viewModel.calculateCgpa() },
                    modifier = Modifier.weight(0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = calculatedCgpa != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 50 })
            ) {
                calculatedCgpa?.let { cgpa ->
                    ResultCard(cgpa = cgpa)
                }
            }
        }
    }
}

@Composable
fun ResultCard(cgpa: Float) {
    val status = when {
        cgpa >= 9 -> "Excellent"
        cgpa >= 8 -> "Very Good"
        cgpa >= 7 -> "Good"
        else -> "Improve"
    }

    val statusColor = when {
        cgpa >= 9 -> Color(0xFF4CAF50)
        cgpa >= 8 -> Color(0xFF2196F3)
        cgpa >= 7 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your CGPA",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = String.format("%.2f", cgpa),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = status,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}
