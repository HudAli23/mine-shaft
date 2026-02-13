package com.example.pickaxeinthemineshaft.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date
import java.time.DayOfWeek

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    @ColumnInfo(name = "dueDate")
    val dueDate: Date? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val category: String = "",
    @ColumnInfo(name = "reminderTime")
    val reminderTime: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    // Recurrence fields
    val frequency: TaskFrequency = TaskFrequency.NONE,
    val scheduledDays: Set<DayOfWeek> = emptySet(),
    val streak: Int = 0,
    val lastCompletedDate: Date? = null
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

enum class TaskFrequency {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}