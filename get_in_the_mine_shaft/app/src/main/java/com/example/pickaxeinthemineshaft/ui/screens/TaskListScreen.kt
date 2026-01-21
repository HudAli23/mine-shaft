package com.example.pickaxeinthemineshaft.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickaxeinthemineshaft.data.Priority
import com.example.pickaxeinthemineshaft.data.Task
import com.example.pickaxeinthemineshaft.ui.viewmodels.TaskListViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var quickAddTitle by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Pair<Long, String>?>(null) }
    var recentlyDeletedTask by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Quick Add Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = quickAddTitle,
                    onValueChange = { quickAddTitle = it },
                    label = { Text("Quick Add Task") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (quickAddTitle.isNotBlank()) {
                            // Use a simple createTask function (you may need to add this to your ViewModel)
                            viewModel.createQuickTask(quickAddTitle)
                            quickAddTitle = ""
                        }
                    },
                    enabled = quickAddTitle.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Quick Add")
                }
            }

            TaskListHeader(
                onFilterChange = viewModel::updateFilter,
                currentFilter = uiState.filter
            )

            if (uiState.tasks.isEmpty()) {
                EmptyTaskList()
            } else {
                TaskList(
                    tasks = uiState.tasks,
                    onTaskClick = onNavigateToEditTask,
                    onTaskComplete = viewModel::toggleTaskCompletion,
                    onTaskDelete = { id, title -> showDeleteDialog = id to title }
                )
            }
        }

        // Delete confirmation dialog
        showDeleteDialog?.let { (taskId, taskTitle) ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete '$taskTitle'?") },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            recentlyDeletedTask = viewModel.deleteTaskAndReturn(taskId)
                            showDeleteDialog = null
                            val result = scaffoldState.snackbarHostState.showSnackbar(
                                message = "Task deleted",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                recentlyDeletedTask?.let { viewModel.restoreTask(it) }
                            }
                        }
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun TaskListHeader(
    onFilterChange: (TaskListFilter) -> Unit,
    currentFilter: TaskListFilter
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.h4
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.padding(end = 8.dp),
                onClick = { onFilterChange(TaskListFilter.ALL) },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = if (currentFilter == TaskListFilter.ALL) 
                        MaterialTheme.colors.primary else MaterialTheme.colors.surface
                )
            ) {
                Text("All", color = if (currentFilter == TaskListFilter.ALL) 
                    MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface)
            }
            OutlinedButton(
                modifier = Modifier.padding(end = 8.dp),
                onClick = { onFilterChange(TaskListFilter.ACTIVE) },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = if (currentFilter == TaskListFilter.ACTIVE) 
                        MaterialTheme.colors.primary else MaterialTheme.colors.surface
                )
            ) {
                Text("Active", color = if (currentFilter == TaskListFilter.ACTIVE) 
                    MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface)
            }
            OutlinedButton(
                onClick = { onFilterChange(TaskListFilter.COMPLETED) },
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = if (currentFilter == TaskListFilter.COMPLETED) 
                        MaterialTheme.colors.primary else MaterialTheme.colors.surface
                )
            ) {
                Text("Completed", color = if (currentFilter == TaskListFilter.COMPLETED) 
                    MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface)
            }
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    onTaskComplete: (Long, Boolean) -> Unit,
    onTaskDelete: (Long, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onClick = { onTaskClick(task.id) },
                onComplete = { completed -> onTaskComplete(task.id, completed) },
                onDelete = { onTaskDelete(task.id, task.title) }
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onComplete: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onComplete(it) }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.body1,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                task.dueDate?.let { dueDate ->
                    Text(
                        text = "Due: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(dueDate)}",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete Task")
            }
            PriorityIndicator(task.priority)
        }
    }
}

@Composable
private fun PriorityIndicator(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> MaterialTheme.colors.error
        Priority.MEDIUM -> MaterialTheme.colors.primary
        Priority.LOW -> MaterialTheme.colors.secondary
    }
    
    Box(
        modifier = Modifier
            .size(12.dp)
            .padding(start = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (priority) {
                Priority.HIGH -> Icons.Default.Warning
                Priority.MEDIUM -> Icons.Default.Close
                Priority.LOW -> Icons.Default.KeyboardArrowDown
            },
            contentDescription = "Priority ${priority.name}",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun EmptyTaskList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colors.secondary
            )
            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Add a task to get started",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

enum class TaskListFilter {
    ALL,
    ACTIVE,
    COMPLETED
} 