package com.example.pickaxeinthemineshaft.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickaxeinthemineshaft.data.Priority
import com.example.pickaxeinthemineshaft.ui.viewmodels.EditTaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.time.DayOfWeek
import com.example.pickaxeinthemineshaft.data.TaskFrequency
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext

@Composable
fun EditTaskScreen(
    taskId: Long,
    onNavigateBack: () -> Unit,
    viewModel: EditTaskViewModel = hiltViewModel()
) {
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var priority by remember { mutableStateOf(uiState.priority) }
    var dueDate by remember { mutableStateOf(uiState.dueDate) }
    var frequency by remember { mutableStateOf(uiState.frequency ?: TaskFrequency.NONE) }
    var scheduledDays by remember { mutableStateOf(uiState.scheduledDays ?: emptySet<DayOfWeek>()) }
    var reminderHour by remember { mutableStateOf(uiState.reminderHour) }
    var reminderMinute by remember { mutableStateOf(uiState.reminderMinute) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val selectedCalendar = Calendar.getInstance()
    val year = selectedCalendar.get(Calendar.YEAR)
    val month = selectedCalendar.get(Calendar.MONTH)
    val day = selectedCalendar.get(Calendar.DAY_OF_MONTH)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteTask(taskId)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            Text(
                text = "Priority",
                style = MaterialTheme.typography.h6
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.values().forEach { priorityOption ->
                    OutlinedButton(
                        onClick = { 
                            priority = priorityOption
                            viewModel.updatePriority(priorityOption)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = if (priority == priorityOption) 
                                MaterialTheme.colors.primary else MaterialTheme.colors.surface
                        )
                    ) {
                        Text(
                            text = priorityOption.name,
                            color = if (priority == priorityOption) 
                                MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
            
            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth, 23, 59, 59)
                            dueDate = selectedCalendar.time
                            viewModel.updateDueDate(selectedCalendar.time)
                        },
                        year, month, day
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = (dueDate ?: uiState.dueDate)?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "Set Due Date"
                )
            }
            
            Text("Repeat", style = MaterialTheme.typography.h6)
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(frequency.name)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                TaskFrequency.values().forEach { freq ->
                    DropdownMenuItem(onClick = {
                        frequency = freq
                        expanded = false
                    }) {
                        Text(freq.name)
                    }
                }
            }
            if (frequency == TaskFrequency.CUSTOM) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DayOfWeek.values().forEach { day ->
                        OutlinedButton(
                            onClick = {
                                scheduledDays = if (scheduledDays.contains(day)) scheduledDays - day else scheduledDays + day
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = if (scheduledDays.contains(day)) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = day.name.take(3),
                                color = if (scheduledDays.contains(day)) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            reminderHour = hourOfDay
                            reminderMinute = minute
                        },
                        reminderHour ?: 9,
                        reminderMinute ?: 0,
                        false // is24HourView
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.reminderTime?.let { SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault()).format(it) } ?: "Set Reminder Time"
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    viewModel.saveTask(
                        taskId = taskId,
                        title = uiState.title,
                        description = uiState.description,
                        priority = priority,
                        dueDate = dueDate,
                        frequency = frequency,
                        scheduledDays = scheduledDays,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    )
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.title.isNotBlank()
            ) {
                Text("Save Changes")
            }
        }
    }
}