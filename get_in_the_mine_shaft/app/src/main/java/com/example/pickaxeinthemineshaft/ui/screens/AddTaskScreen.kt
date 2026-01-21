package com.example.pickaxeinthemineshaft.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// No need for TextFieldValue import
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickaxeinthemineshaft.data.Priority
import com.example.pickaxeinthemineshaft.ui.viewmodels.AddTaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.time.DayOfWeek
import com.example.pickaxeinthemineshaft.data.TaskFrequency
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddTaskScreen(
    onTaskAdded: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    // Recurrence fields
    var frequency by remember { mutableStateOf(TaskFrequency.NONE) }
    var scheduledDays by remember { mutableStateOf(emptySet<DayOfWeek>()) }
    var reminderHour by remember { mutableStateOf<Int?>(null) }
    var reminderMinute by remember { mutableStateOf<Int?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Task") },
                navigationIcon = {
                    IconButton(onClick = onTaskAdded) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            
            TextField(
                value = description,
                onValueChange = { description = it },
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
                        onClick = { priority = priorityOption },
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
                    text = dueDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "Set Due Date"
                )
            }
            
            // Recurrence Dropdown
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
                    text = if (reminderHour != null && reminderMinute != null) {
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, reminderHour!!)
                            set(Calendar.MINUTE, reminderMinute!!)
                        }
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedCalendar.time)
                    } else "Set Reminder Time"
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.createTask(
                            title = title,
                            description = description,
                            priority = priority,
                            dueDate = dueDate,
                            frequency = frequency,
                            scheduledDays = scheduledDays,
                            reminderHour = reminderHour,
                            reminderMinute = reminderMinute
                        )
                        onTaskAdded()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Create Task")
            }
        }
    }
}