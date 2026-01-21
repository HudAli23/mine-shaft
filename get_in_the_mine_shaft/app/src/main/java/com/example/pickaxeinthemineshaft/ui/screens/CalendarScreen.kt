package com.example.pickaxeinthemineshaft.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickaxeinthemineshaft.data.Task
import com.example.pickaxeinthemineshaft.ui.viewmodels.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDate by remember { mutableStateOf(uiState.selectedDate) }
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    // Get first day of month and number of days
    val firstDayOfMonth = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentYear)
        set(Calendar.MONTH, currentMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, ...

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Month and year header with navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val cal = Calendar.getInstance().apply { time = selectedDate }
                cal.add(Calendar.MONTH, -1)
                selectedDate = cal.time
                viewModel.selectDate(selectedDate)
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(selectedDate),
                style = MaterialTheme.typography.h6
            )
            IconButton(onClick = {
                val cal = Calendar.getInstance().apply { time = selectedDate }
                cal.add(Calendar.MONTH, 1)
                selectedDate = cal.time
                viewModel.selectDate(selectedDate)
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colors.primary
                )
            }
        }

        // Calendar grid
        val totalCells = daysInMonth + (firstDayOfWeek - 1)
        val weeks = ceil(totalCells / 7.0).toInt()
        Column {
            var dayCounter = 1
            for (week in 0 until weeks) {
                Row(Modifier.fillMaxWidth()) {
                    for (dow in 1..7) {
                        val cellIndex = week * 7 + dow
                        if (cellIndex < firstDayOfWeek || dayCounter > daysInMonth) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f)) {}
                        } else {
                            val cal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, currentYear)
                                set(Calendar.MONTH, currentMonth)
                                set(Calendar.DAY_OF_MONTH, dayCounter)
                            }
                            val isSelected = SimpleDateFormat("yyyyMMdd").format(selectedDate) == SimpleDateFormat("yyyyMMdd").format(cal.time)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colors.primary else Color.Transparent)
                                    .clickable {
                                        selectedDate = cal.time
                                        viewModel.selectDate(selectedDate)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayCounter.toString(),
                                    color = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                                )
                            }
                            dayCounter++
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tasks for ${SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(selectedDate)}",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks scheduled for this day",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.tasks) { task ->
                    CalendarTaskItem(task = task)
                }
            }
        }
    }
}

@Composable
private fun CalendarTaskItem(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (task.priority) {
                        com.example.pickaxeinthemineshaft.data.Priority.HIGH -> Icons.Default.Warning
                        com.example.pickaxeinthemineshaft.data.Priority.MEDIUM -> Icons.Default.Close
                        com.example.pickaxeinthemineshaft.data.Priority.LOW -> Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = "Priority ${task.priority.name}",
                    tint = when (task.priority) {
                        com.example.pickaxeinthemineshaft.data.Priority.HIGH -> MaterialTheme.colors.error
                        com.example.pickaxeinthemineshaft.data.Priority.MEDIUM -> MaterialTheme.colors.primary
                        com.example.pickaxeinthemineshaft.data.Priority.LOW -> MaterialTheme.colors.secondary
                    }
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.body2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                task.dueDate?.let { dueDate ->
                    Text(
                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dueDate),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 16.dp)
                )
            }
        }
    }
} 