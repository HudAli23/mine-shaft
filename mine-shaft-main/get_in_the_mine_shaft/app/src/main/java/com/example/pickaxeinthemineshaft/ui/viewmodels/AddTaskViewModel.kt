package com.example.pickaxeinthemineshaft.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickaxeinthemineshaft.data.Priority
import com.example.pickaxeinthemineshaft.data.Task
import com.example.pickaxeinthemineshaft.data.TaskRepository
import com.example.pickaxeinthemineshaft.data.TaskFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    fun createTask(
        title: String,
        description: String,
        priority: Priority,
        dueDate: Date?,
        frequency: TaskFrequency = TaskFrequency.NONE,
        scheduledDays: Set<DayOfWeek> = emptySet(),
        reminderHour: Int? = null,
        reminderMinute: Int? = null
    ) {
        if (title.isBlank()) {
            Log.d("AddTaskViewModel", "Title is blank, not creating task")
            return
        }
        Log.d("AddTaskViewModel", "Creating task: $title")
        viewModelScope.launch {
            val reminderTime = if (reminderHour != null && reminderMinute != null) {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, reminderHour)
                    set(Calendar.MINUTE, reminderMinute)
                }.time
            } else null
            val task = Task(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                frequency = frequency,
                scheduledDays = scheduledDays,
                reminderTime = reminderTime
            )
            Log.d("AddTaskViewModel", "Inserting task: $task")
            taskRepository.createTask(task)
        }
    }
} 