package com.example.pickaxeinthemineshaft.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickaxeinthemineshaft.data.Priority
import com.example.pickaxeinthemineshaft.data.Task
import com.example.pickaxeinthemineshaft.data.TaskRepository
import com.example.pickaxeinthemineshaft.data.TaskFrequency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.time.DayOfWeek
import javax.inject.Inject

data class EditTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Date? = null,
    val frequency: TaskFrequency = TaskFrequency.NONE,
    val scheduledDays: Set<DayOfWeek> = emptySet(),
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val reminderTime: Date? = null
)

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId)?.let { task ->
                val calendar = Calendar.getInstance().apply { time = task.reminderTime ?: Date() }
                _uiState.value = EditTaskUiState(
                    title = task.title,
                    description = task.description,
                    priority = task.priority,
                    dueDate = task.dueDate,
                    frequency = task.frequency,
                    scheduledDays = task.scheduledDays,
                    reminderHour = task.reminderTime?.let { calendar.get(Calendar.HOUR_OF_DAY) },
                    reminderMinute = task.reminderTime?.let { calendar.get(Calendar.MINUTE) },
                    reminderTime = task.reminderTime
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updatePriority(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun updateDueDate(dueDate: Date?) {
        _uiState.value = _uiState.value.copy(dueDate = dueDate)
    }

    fun saveTask(
        taskId: Long,
        title: String,
        description: String,
        priority: Priority,
        dueDate: Date?,
        frequency: TaskFrequency = TaskFrequency.NONE,
        scheduledDays: Set<DayOfWeek> = emptySet(),
        reminderHour: Int? = null,
        reminderMinute: Int? = null
    ) {
        viewModelScope.launch {
            val reminderTime = if (reminderHour != null && reminderMinute != null) {
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, reminderHour)
                    set(Calendar.MINUTE, reminderMinute)
                }.time
            } else null
            val task = Task(
                id = taskId,
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                frequency = frequency,
                scheduledDays = scheduledDays,
                reminderTime = reminderTime
            )
            taskRepository.updateTask(task)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId)?.let { task ->
                taskRepository.deleteTask(task)
            }
        }
    }
} 