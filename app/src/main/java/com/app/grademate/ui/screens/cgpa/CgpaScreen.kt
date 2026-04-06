package com.app.grademate.ui.screens.cgpa

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.app.grademate.ui.theme.BlueSky

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CgpaScreen(
    navController: NavController,
    viewModel: CgpaViewModel
) {
    val subjects by viewModel.subjects.collectAsState()
    val gradeS by viewModel.gradeS.collectAsState()
    val gradeA by viewModel.gradeA.collectAsState()
    val gradeB by viewModel.gradeB.collectAsState()
    val gradeC by viewModel.gradeC.collectAsState()
    val gradeD by viewModel.gradeD.collectAsState()
    val gradeE by viewModel.gradeE.collectAsState()
    
    val totalSubjects by viewModel.totalSubjects.collectAsState()
    val totalCredits by viewModel.totalCredits.collectAsState()
    val calculatedCgpa by viewModel.calculatedCgpa.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val lastCgpa by viewModel.lastCgpa.collectAsState()

    Scaffold(
        topBar = {
            AppTopBarWrapper(
                title = "CGPA Calculator",
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
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Text(
                        text = "Last: ${String.format("%.2f", lastCgpa)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Surface(
                    color = BlueSky.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(text = "Credits: ", fontSize = 14.sp, color = Color.Gray)
                        Text(text = "$totalCredits", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BlueSky)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Add Grades
            Text(text = "Add Subjects", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val gradeCounts = mapOf(
                    "S" to gradeS,
                    "A" to gradeA,
                    "B" to gradeB,
                    "C" to gradeC,
                    "D" to gradeD,
                    "E" to gradeE
                )
                listOf("S", "A", "B", "C", "D", "E").forEach { grade ->
                    GradePickItem(
                        grade = grade,
                        count = gradeCounts[grade] ?: 0,
                        onClick = { viewModel.addSubject(grade) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subject List
            Box(modifier = Modifier.weight(1f)) {
                if (subjects.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No subjects added yet", color = Color.LightGray, fontSize = 16.sp)
                        Text(text = "Tap a grade above to start", color = Color.LightGray, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(subjects, key = { it.id }) { subject ->
                            SubjectItem(
                                modifier = Modifier.animateItem(),
                                subject = subject,
                                onCreditsChange = { viewModel.updateSubjectCredits(subject.id, it) },
                                onDelete = { viewModel.removeSubject(subject.id) }
                            )
                        }
                    }
                }
            }

            // Calculation and Results
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.weight(0.35f).height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Text("Reset")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    GradientButton(
                        text = "Calculate",
                        onClick = { viewModel.calculateCgpa() },
                        modifier = Modifier.weight(0.65f).height(54.dp)
                    )
                }

                AnimatedVisibility(
                    visible = calculatedCgpa != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    calculatedCgpa?.let { ResultCard(cgpa = it) }
                }
            }
        }
    }
}

@Composable
fun GradePickItem(grade: String, count: Int, onClick: () -> Unit) {
    BadgedBox(
        badge = {
            if (count > 0) {
                Badge(
                    containerColor = Color(0xFFEF5350),
                    contentColor = Color.White,
                    modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                ) {
                    Text(text = count.toString(), fontSize = 10.sp)
                }
            }
        }
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = grade, fontWeight = FontWeight.Bold, color = BlueSky, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun SubjectItem(
    modifier: Modifier = Modifier,
    subject: Subject,
    onCreditsChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = BlueSky.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = subject.grade, fontWeight = FontWeight.Bold, color = BlueSky)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Credits", fontSize = 12.sp, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3, 4).forEach { credit ->
                        CreditChip(
                            value = credit,
                            isSelected = subject.credits == credit,
                            onSelect = { onCreditsChange(credit) }
                        )
                    }
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF5350))
            }
        }
    }
}

@Composable
fun CreditChip(value: Int, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) BlueSky else Color(0xFFF1F5F9),
        modifier = Modifier.size(width = 32.dp, height = 28.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$value",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}

@Composable
fun ResultCard(cgpa: Float) {
    val status = when {
        cgpa >= 9 -> "Outstanding"
        cgpa >= 8 -> "Excellent"
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
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Estimated CGPA", fontSize = 14.sp, color = Color.Gray)
            
            AnimatedContent(
                targetState = cgpa,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.8f) togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                label = "cgpa_anim"
            ) { targetCgpa ->
                Text(
                    text = String.format("%.2f", targetCgpa),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BlueSky
                )
            }

            Text(
                text = status,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}
