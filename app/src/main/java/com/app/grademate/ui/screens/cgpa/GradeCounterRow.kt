package com.app.grademate.ui.screens.cgpa

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grademate.ui.components.CounterButton

@Composable
fun GradeCounterRow(
    grade: String,
    point: Int,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = grade,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(40.dp)
        )
        
        Text(
            text = "($point)",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CounterButton(
                icon = Icons.Default.Remove,
                onClick = onDecrement,
                enabled = count > 0,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(32.dp)
            )
            
            CounterButton(
                icon = Icons.Default.Add,
                onClick = onIncrement,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
