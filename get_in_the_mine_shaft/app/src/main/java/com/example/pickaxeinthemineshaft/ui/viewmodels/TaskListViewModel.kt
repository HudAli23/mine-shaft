package com.example.pickaxeinthemineshaft.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickaxeinthemineshaft.data.Task
import com.example.pickaxeinthemineshaft.data.TaskRepository
import com.example.pickaxeinthemineshaft.ui.screens.TaskListFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _filter = MutableStateFlow(TaskListFilter.ALL)
    private val _tasks = taskRepository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val uiState: StateFlow<TaskListUiState> = combine(
        _tasks,
        _filter
    ) { tasks, filter ->
        val filteredTasks = when (filter) {
            TaskListFilter.ALL -> tasks
            TaskListFilter.ACTIVE -> tasks.filter { !it.isCompleted }
            TaskListFilter.COMPLETED -> tasks.filter { it.isCompleted }
        }
        TaskListUiState(
            tasks = filteredTasks,
            filter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskListUiState()
    )
    
    fun updateFilter(filter: TaskListFilter) {
        _filter.value = filter
    }
    
    fun toggleTaskCompletion(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId)?.let { task ->
                taskRepository.updateTask(task.copy(isCompleted = completed))
            }
        }
    }

    fun createQuickTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val task = Task(title = title)
            taskRepository.createTask(task)
        }
    }

    suspend fun deleteTaskAndReturn(taskId: Long): Task? {
        var deletedTask: Task? = null
        viewModelScope.launch {
            deletedTask = taskRepository.getTaskById(taskId)
            deletedTask?.let { taskRepository.deleteTask(it) }
        }.join()
        return deletedTask
    }

    fun restoreTask(task: Task) {
        viewModelScope.launch {
            taskRepository.createTask(task)
        }
    }
}

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val filter: TaskListFilter = TaskListFilter.ALL
) 