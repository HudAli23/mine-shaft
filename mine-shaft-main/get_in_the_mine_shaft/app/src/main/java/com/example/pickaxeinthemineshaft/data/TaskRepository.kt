package com.example.pickaxeinthemineshaft.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TaskRepository"

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    // Repository wraps DAO calls, exposing cold Flows with error-catching
    // so UI layers don’t crash on DB issues.
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
        .catch { e ->
            Log.e(TAG, "Error getting all tasks", e)
            emit(emptyList())
        }
    
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
        .catch { e ->
            Log.e(TAG, "Error getting active tasks", e)
            emit(emptyList())
        }
    
    suspend fun getTaskById(taskId: Long): Task? = try {
        taskDao.getTaskById(taskId)
    } catch (e: Exception) {
        Log.e(TAG, "Error getting task by id: $taskId", e)
        null
    }
    
    suspend fun createTask(task: Task): Long = try {
        Log.d("TaskRepository", "Inserting task into DB: $task")
        taskDao.insertTask(task).also {
            Log.d("TaskRepository", "Task created successfully with id: $it")
        }
    } catch (e: Exception) {
        Log.e("TaskRepository", "Error creating task", e)
        throw e
    }
    
    suspend fun updateTask(task: Task) = try {
        taskDao.updateTask(task)
        Log.d(TAG, "Task updated successfully: ${task.id}")
    } catch (e: Exception) {
        Log.e(TAG, "Error updating task: ${task.id}", e)
        throw e
    }
    
    suspend fun deleteTask(task: Task) = try {
        taskDao.deleteTask(task)
        Log.d(TAG, "Task deleted successfully: ${task.id}")
    } catch (e: Exception) {
        Log.e(TAG, "Error deleting task: ${task.id}", e)
        throw e
    }
    
    fun getTasksInDateRange(start: Date, end: Date): Flow<List<Task>> =
        taskDao.getTasksInDateRange(start, end)
            .catch { e ->
                Log.e(TAG, "Error getting tasks in date range", e)
                emit(emptyList())
            }
    
    fun getTasksByCategory(category: String): Flow<List<Task>> =
        taskDao.getTasksByCategory(category)
            .catch { e ->
                Log.e(TAG, "Error getting tasks by category: $category", e)
                emit(emptyList())
            }
    
    suspend fun updateTaskCompletion(taskId: Long, completed: Boolean) = try {
        taskDao.updateTaskCompletion(taskId, completed)
        Log.d(TAG, "Task completion updated successfully: $taskId to $completed")
    } catch (e: Exception) {
        Log.e(TAG, "Error updating task completion: $taskId", e)
        throw e
    }
} 