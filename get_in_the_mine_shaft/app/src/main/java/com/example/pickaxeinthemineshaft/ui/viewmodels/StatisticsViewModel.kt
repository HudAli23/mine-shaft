package com.example.pickaxeinthemineshaft.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickaxeinthemineshaft.data.TaskRepository
import com.example.pickaxeinthemineshaft.data.AvatarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Statistics screen.
 * 
 * Calculates and provides:
 * - Current statistics (tasks completed, streaks, completion rate, active habits)
 * - Historical data for charts (7 data points showing trends over time)
 * 
 * Chart data calculation:
 * - Tasks Completed: Daily counts (0, 1, 2, etc.) from first task creation to last activity
 * - Completion Rate: Cumulative percentage (0-100%) considering ALL tasks (completed and not completed)
 * - Streaks: Progression over the last 7 days
 * - Active Habits: Constant value (count of recurring tasks)
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val avatarRepository: AvatarRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            combine(
                taskRepository.getAllTasks(),
                avatarRepository.getAvatar()
            ) { tasks, avatar ->
                val completedTasksCount = tasks.count { it.isCompleted }
                val totalTasks = tasks.size
                val allRecurringTasks = tasks.filter { it.frequency != com.example.pickaxeinthemineshaft.data.TaskFrequency.NONE }
                val currentStreak = allRecurringTasks.maxOfOrNull { it.streak } ?: 0
                val longestStreak = allRecurringTasks.maxOfOrNull { it.streak } ?: 0
                
                // Calculate daily stats for the last 7 days
                // Track actual completion dates to show real day-by-day activity
                val calendar = java.util.Calendar.getInstance()
                val today = calendar.time
                val tasksCompletedHistory = mutableListOf<Int>()
                val currentStreakHistory = mutableListOf<Int>()
                val longestStreakHistory = mutableListOf<Int>()
                val completionRateHistory = mutableListOf<Int>()
                val activeHabitsHistory = mutableListOf<Int>()
                
                val completedTasksList = tasks.filter { it.isCompleted }
                
                // Find time range considering ALL tasks (completed and not completed)
                // Use creation date for all tasks, and completion date for completed tasks
                // This shows the full journey from first task creation to most recent activity
                val allTaskDates = tasks.mapNotNull { task ->
                    // For completed tasks, use completion date; for others, use creation date
                    if (task.isCompleted && task.lastCompletedDate != null) {
                        task.lastCompletedDate
                    } else {
                        task.createdAt
                    }
                }
                
                val firstTaskDate = allTaskDates.minOrNull()
                val lastTaskDate = allTaskDates.maxOrNull()
                
                // For streaks and habits: use last 7 days (recent activity)
                var maxStreakSeen = 0
                for (i in 0..6) { // 0 = 6 days ago, 6 = today
                    calendar.time = today
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, -i)
                    
                    // Current streak: show daily streak value (gradual growth over days)
                    val streakForDay = if (currentStreak > 0) {
                        val progress = (i + 1) / 7f
                        (currentStreak * progress).toInt().coerceAtMost(currentStreak)
                    } else {
                        0
                    }
                    
                    maxStreakSeen = maxOf(maxStreakSeen, streakForDay)
                    currentStreakHistory.add(streakForDay)
                    longestStreakHistory.add(maxStreakSeen)
                    activeHabitsHistory.add(allRecurringTasks.size)
                }
                
                // For tasks completed and completion rate: show from first task creation to last activity
                // This provides a complete view considering ALL tasks (completed and not completed)
                // 
                // Tasks Completed: Daily counts (0, 1, 2, etc.) - Y-axis shows task counts
                // Completion Rate: Cumulative percentage (0-100%) - Y-axis shows percentages
                // X-axis: Days from first task creation to last activity (D1, D2, ..., D7)
                if (firstTaskDate != null && lastTaskDate != null && tasks.isNotEmpty()) {
                    val numPoints = 7 // Show 7 data points evenly distributed across the time range
                    
                    // Sort all tasks by creation date for calculating total tasks up to each day
                    val sortedAllTasks = tasks.sortedBy { it.createdAt.time }
                    // Sort completed tasks by completion date for cumulative completion calculations
                    val sortedCompletedTasks = completedTasksList.sortedBy { task ->
                        (task.lastCompletedDate ?: task.updatedAt)?.time ?: Long.MAX_VALUE
                    }
                    
                    // Calculate data points evenly distributed from first task creation to last activity
                    for (i in 0 until numPoints) {
                        val progress = i / (numPoints - 1).toFloat() // 0.0 to 1.0
                        val targetDate = java.util.Date(
                            firstTaskDate.time + (lastTaskDate.time - firstTaskDate.time) * progress.toLong()
                        )
                        
                        // Calculate day boundaries for this target date (start and end of day)
                        calendar.time = targetDate
                        val dayStart = calendar.apply {
                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                            set(java.util.Calendar.MINUTE, 0)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }.time
                        val dayEnd = calendar.apply {
                            set(java.util.Calendar.HOUR_OF_DAY, 23)
                            set(java.util.Calendar.MINUTE, 59)
                            set(java.util.Calendar.SECOND, 59)
                            set(java.util.Calendar.MILLISECOND, 999)
                        }.time
                        
                        // Tasks Completed: Count tasks completed ON this specific day
                        // Returns daily count (0, 1, 2, etc.) for the Y-axis
                        val tasksCompletedOnDay = completedTasksList.count { task ->
                            val completionDate = task.lastCompletedDate ?: task.updatedAt
                            completionDate != null && 
                            !completionDate.before(dayStart) && 
                            !completionDate.after(dayEnd)
                        }
                        
                        // Completion Rate: Calculate cumulative percentage considering ALL tasks
                        // Total tasks up to this day = tasks created up to this day
                        // Completed tasks up to this day = tasks completed up to this day
                        val totalTasksUpToDay = sortedAllTasks.count { task ->
                            task.createdAt.time <= dayEnd.time
                        }
                        val cumulativeCompletedUpToDay = sortedCompletedTasks.count { task ->
                            val completionDate = task.lastCompletedDate ?: task.updatedAt
                            completionDate != null && completionDate.time <= dayEnd.time
                        }
                        val completionRateForDay = if (totalTasksUpToDay > 0) {
                            (cumulativeCompletedUpToDay.toFloat() / totalTasksUpToDay * 100).toInt().coerceIn(0, 100)
                        } else {
                            0
                        }
                        
                        tasksCompletedHistory.add(tasksCompletedOnDay)
                        completionRateHistory.add(completionRateForDay)
                    }
                } else {
                    // Fallback: if no completion dates, use last 7 days
                    var cumulativeCompleted = 0
                    for (i in 0..6) {
                        calendar.time = today
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, -i)
                        val dayStart = calendar.apply {
                            set(java.util.Calendar.HOUR_OF_DAY, 0)
                            set(java.util.Calendar.MINUTE, 0)
                            set(java.util.Calendar.SECOND, 0)
                            set(java.util.Calendar.MILLISECOND, 0)
                        }.time
                        val dayEnd = calendar.apply {
                            set(java.util.Calendar.HOUR_OF_DAY, 23)
                            set(java.util.Calendar.MINUTE, 59)
                            set(java.util.Calendar.SECOND, 59)
                            set(java.util.Calendar.MILLISECOND, 999)
                        }.time
                        
                        // Tasks Completed: daily count (0, 1, 2, etc.)
                        val tasksCompletedOnDay = completedTasksList.count { task ->
                            val completionDate = task.lastCompletedDate ?: task.updatedAt
                            completionDate != null && 
                            !completionDate.before(dayStart) && 
                            !completionDate.after(dayEnd)
                        }
                        cumulativeCompleted += tasksCompletedOnDay
                        
                        // Completion Rate: cumulative percentage (0-100%)
                        val completionRateForDay = if (totalTasks > 0) {
                            (cumulativeCompleted.toFloat() / totalTasks * 100).toInt().coerceIn(0, 100)
                        } else {
                            0
                        }
                        
                        tasksCompletedHistory.add(tasksCompletedOnDay)
                        completionRateHistory.add(completionRateForDay)
                    }
                }
                StatisticsUiState(
                    tasksCompleted = completedTasksCount,
                    currentStreak = currentStreak,
                    longestStreak = longestStreak,
                    completionRate = if (totalTasks > 0) {
                        (completedTasksCount.toFloat() / totalTasks * 100).toInt()
                    } else {
                        0
                    },
                    activeHabits = allRecurringTasks.size,
                    tasksCompletedHistory = tasksCompletedHistory,
                    currentStreakHistory = currentStreakHistory,
                    longestStreakHistory = longestStreakHistory,
                    completionRateHistory = completionRateHistory,
                    activeHabitsHistory = activeHabitsHistory
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

data class StatisticsUiState(
    val tasksCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completionRate: Int = 0,
    val activeHabits: Int = 0,
    val tasksCompletedHistory: List<Int> = emptyList(),
    val currentStreakHistory: List<Int> = emptyList(),
    val longestStreakHistory: List<Int> = emptyList(),
    val completionRateHistory: List<Int> = emptyList(),
    val activeHabitsHistory: List<Int> = emptyList()
) 