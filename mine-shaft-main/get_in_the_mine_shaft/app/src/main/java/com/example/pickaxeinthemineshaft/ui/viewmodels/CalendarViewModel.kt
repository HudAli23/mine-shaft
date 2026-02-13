package com.example.pickaxeinthemineshaft.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickaxeinthemineshaft.data.Task
import com.example.pickaxeinthemineshaft.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow(Date())
    private val _tasks = taskRepository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedDate,
        _tasks
    ) { selectedDate, tasks ->
        // Create calendar for start of day
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time
        
        // Create calendar for end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time
        
        val tasksForDay = tasks.filter { task ->
            task.dueDate?.let { dueDate ->
                dueDate.after(startOfDay) && dueDate.before(endOfDay) || 
                dueDate.time == startOfDay.time || dueDate.time == endOfDay.time
            } ?: false
        }.sortedBy { it.dueDate }
        
        CalendarUiState(
            selectedDate = selectedDate,
            tasks = tasksForDay
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )
    
    fun selectDate(date: Date) {
        _selectedDate.value = date
    }
    
    fun selectNextDay() {
        val calendar = Calendar.getInstance()
        calendar.time = _selectedDate.value
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        _selectedDate.value = calendar.time
    }
    
    fun selectPreviousDay() {
        val calendar = Calendar.getInstance()
        calendar.time = _selectedDate.value
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        _selectedDate.value = calendar.time
    }
}

data class CalendarUiState(
    val selectedDate: Date = Date(),
    val tasks: List<Task> = emptyList()
) 