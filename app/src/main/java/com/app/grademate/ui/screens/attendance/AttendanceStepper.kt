package com.app.grademate.ui.screens.attendance

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grademate.ui.components.CounterButton

@Composable
fun AttendanceStepper(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        CounterButton(
            icon = Icons.Default.Remove,
            onClick = { onValueChange((value - 1).coerceAtLeast(min)) },
            enabled = value > min,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Text(
            text = value.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(48.dp)
        )
        
        CounterButton(
            icon = Icons.Default.Add,
            onClick = { onValueChange((value + 1).coerceAtMost(max)) },
            enabled = value < max,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
