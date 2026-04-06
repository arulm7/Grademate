package com.app.grademate.ui.screens.attendance

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.grademate.ui.components.AppTopBarWrapper
import com.app.grademate.ui.components.GradientButton

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
        displayPercentage < 75 -> Color.Red
        displayPercentage < 80 -> Color(0xFFFFA000) // Amber/Yellow
        else -> Color.Green
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
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
                    
                    Slider(
                        value = attendedClasses.toFloat(),
                        onValueChange = { viewModel.updateAttendedClasses(it.toInt()) },
                        valueRange = 0f..totalClasses.toFloat().coerceAtLeast(1f),
                        steps = if (totalClasses > 1) totalClasses - 1 else 0,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )
                }
            }
            
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    AttendanceStepper(
                        label = "Future Predictor",
                        value = futureClasses,
                        onValueChange = viewModel::updateFutureClasses,
                        min = 0
                    )
                }
            }

            AnimatedVisibility(
                visible = attendancePercentage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 50 })
            ) {
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
                        
                        val animatedProgress by animateFloatAsState(
                            targetValue = if (displayPercentage > 0f) (displayPercentage / 100f) else 0f,
                            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                            label = "circular_progress"
                        )

                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.size(150.dp),
                                color = statusColor,
                                trackColor = Color.LightGray,
                                strokeWidth = 12.dp,
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AnimatedContent(targetState = displayPercentage, label = "percentage_anim") { percentage ->
                                    Text(
                                        text = String.format("%.1f%%", percentage),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = statusText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (displayPercentage < 80) {
                            AnimatedContent(targetState = classesNeeded, label = "classes_needed_anim") { needed ->
                                Text(
                                    text = "You need $needed more classes to reach 80%",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Text(
                                text = "Keep it up!",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (predictionIfAttend != null && predictionIfMiss != null && futureClasses > 0) {
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Predictions",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                AnimatedContent(targetState = predictionIfAttend!!, label = "pred_attend") { pred ->
                                    Text(
                                        text = "If you attend next $futureClasses classes → ${String.format("%.1f%%", pred)}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF4CAF50), // Green
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                AnimatedContent(targetState = predictionIfMiss!!, label = "pred_miss") { pred ->
                                    Text(
                                        text = "If you miss next $futureClasses classes → ${String.format("%.1f%%", pred)}",
                                        fontSize = 14.sp,
                                        color = Color(0xFFF44336), // Red
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        GradientButton(
                            text = "Save Tracked History",
                            onClick = { viewModel.saveHistory() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
