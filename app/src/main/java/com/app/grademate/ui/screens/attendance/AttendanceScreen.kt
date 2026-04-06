package com.app.grademate.ui.screens.attendance

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.grademate.ui.components.AppTopBarWrapper
import com.app.grademate.ui.components.GradientButton
import com.app.grademate.ui.theme.BlueSky

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    navController: NavController,
    viewModel: AttendanceViewModel
) {
    val totalClasses by viewModel.totalClasses.collectAsState()
    val attendedClasses by viewModel.attendedClasses.collectAsState()
    val futureClasses by viewModel.futureClasses.collectAsState()
    val attendancePercentage by viewModel.attendancePercentage.collectAsState()
    val classesNeeded by viewModel.classesNeeded.collectAsState()
    val predictionIfAttend by viewModel.predictionIfAttend.collectAsState()
    val predictionIfMiss by viewModel.predictionIfMiss.collectAsState()

    val displayPercentage = attendancePercentage ?: 0f

    val statusColor = when {
        displayPercentage < 75 -> Color(0xFFEF5350) // Soft Red
        displayPercentage < 80 -> Color(0xFFFFA726) // Soft Orange
        else -> Color(0xFF66BB6A) // Soft Green
    }

    val statusText = when {
        displayPercentage < 75 -> "Low Attendance"
        displayPercentage < 80 -> "Warning Zone"
        else -> "Safe"
    }

    Scaffold(
        topBar = {
            AppTopBarWrapper(
                title = "Attendance Tracker",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp)
                ) {
                    Text(
                        text = "Class Statistics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    AttendanceStepper(
                        label = "Total Classes",
                        value = totalClasses,
                        onValueChange = viewModel::updateTotalClasses,
                        min = 0
                    )
                    
                    AttendanceStepper(
                        label = "Attended",
                        value = attendedClasses,
                        onValueChange = viewModel::updateAttendedClasses,
                        max = totalClasses,
                        min = 0
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Slider(
                        value = attendedClasses.toFloat(),
                        onValueChange = { viewModel.updateAttendedClasses(it.toInt()) },
                        valueRange = 0f..totalClasses.toFloat().coerceAtLeast(1f),
                        steps = if (totalClasses > 1) totalClasses - 1 else 0,
                        colors = SliderDefaults.colors(
                            thumbColor = BlueSky,
                            activeTrackColor = BlueSky,
                            inactiveTrackColor = BlueSky.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            // Predictor Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp)
                ) {
                    Text(
                        text = "Future Predictor",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    AttendanceStepper(
                        label = "Upcoming Classes",
                        value = futureClasses,
                        onValueChange = viewModel::updateFutureClasses,
                        min = 0
                    )
                    Text(
                        text = "Simulate your future attendance based on $futureClasses upcoming classes.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Results Card
            AnimatedVisibility(
                visible = attendancePercentage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val animatedProgress by animateFloatAsState(
                            targetValue = if (displayPercentage > 0f) (displayPercentage / 100f) else 0f,
                            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                            label = "circular_progress"
                        )

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFFF1F5F9),
                                strokeWidth = 16.dp,
                                strokeCap = StrokeCap.Round
                            )
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = statusColor,
                                strokeWidth = 16.dp,
                                strokeCap = StrokeCap.Round
                            )
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedContent(
                                    targetState = displayPercentage,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                                scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) togetherWith
                                                fadeOut(animationSpec = tween(90))
                                    },
                                    label = "percentage_anim"
                                ) { targetPercent ->
                                    Text(
                                        text = String.format("%.0f%%", targetPercent),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.DarkGray
                                    )
                                }
                                Text(
                                    text = statusText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (displayPercentage < 80) {
                            Surface(
                                color = statusColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "You need $classesNeeded more classes for 80%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                )
                            }
                        } else {
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Great job! Your attendance is solid.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                )
                            }
                        }

                        if (predictionIfAttend != null && predictionIfMiss != null && futureClasses > 0) {
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Text(
                                text = "Impact Analysis",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PredictionChart(
                                    label = "If you attend all",
                                    value = predictionIfAttend!!,
                                    color = Color(0xFF66BB6A) // Soft Green
                                )
                                
                                PredictionChart(
                                    label = "If you miss all",
                                    value = predictionIfMiss!!,
                                    color = Color(0xFFEF5350) // Soft Red
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        GradientButton(
                            text = "Save Progress",
                            onClick = { viewModel.saveHistory() },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PredictionChart(label: String, value: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(120.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF1F5F9),
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { value / 100f },
                modifier = Modifier.fillMaxSize(),
                color = color,
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = String.format("%.0f%%", value),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}
