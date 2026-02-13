package com.example.pickaxeinthemineshaft.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.pickaxeinthemineshaft.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val TAG = "AppModule"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Central DI graph: provides Room + DAOs + repositories used across the app.
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return try {
            Log.d(TAG, "Building database instance")
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            )
            .fallbackToDestructiveMigration()
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d(TAG, "Database created successfully")
                    // Seed: pre-populate a small task set so first launch has data to render.
                    for (i in 1..15) {
                        val isCompleted = if (i % 3 != 0) 1 else 0 // 2 out of 3 tasks completed
                        val priority = if (i % 3 == 0) "HIGH" else if (i % 3 == 1) "MEDIUM" else "LOW"
                        val dayOffset = (i - 1) / 3
                        val now = "strftime('%s','now')*1000"
                        val dueDate = "($now - ($dayOffset * 24 * 60 * 60 * 1000))"
                        val reminderTime = "($now - ($dayOffset * 24 * 60 * 60 * 1000) + 8*60*60*1000)"
                        // Every other task is a DAILY habit with a streak
                        val frequency = if (i % 2 == 0) "'DAILY'" else "'NONE'"
                        val streak = if (i % 2 == 0) (i / 2) else 0
                        db.execSQL("INSERT INTO tasks (title, description, dueDate, priority, isCompleted, category, reminderTime, createdAt, updatedAt, frequency, scheduledDays, streak, lastCompletedDate) VALUES (" +
                            "'Task $i', " +
                            "'Test task $i', " +
                            "$dueDate, " +
                            "'$priority', $isCompleted, '', " +
                            "$reminderTime, " +
                            "$now, $now, $frequency, '[]', $streak, NULL);");
                    }
                }

                override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d(TAG, "Database opened successfully")
                }
            })
            .build()
            .also { Log.d(TAG, "Database instance built successfully") }
        } catch (e: Exception) {
            Log.e(TAG, "Error building database", e)
            throw e
        }
    }
    
    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return try {
            database.taskDao().also { Log.d(TAG, "TaskDao provided successfully") }
        } catch (e: Exception) {
            Log.e(TAG, "Error providing TaskDao", e)
            throw e
        }
    }
    
    @Provides
    @Singleton
    fun provideAvatarDao(database: AppDatabase): AvatarDao {
        return try {
            database.avatarDao().also { Log.d(TAG, "AvatarDao provided successfully") }
        } catch (e: Exception) {
            Log.e(TAG, "Error providing AvatarDao", e)
            throw e
        }
    }
    
    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return try {
            TaskRepository(taskDao).also { Log.d(TAG, "TaskRepository provided successfully") }
        } catch (e: Exception) {
            Log.e(TAG, "Error providing TaskRepository", e)
            throw e
        }
    }
    
    @Provides
    @Singleton
    fun provideAvatarRepository(avatarDao: AvatarDao): AvatarRepository {
        return try {
            AvatarRepository(avatarDao).also { Log.d(TAG, "AvatarRepository provided successfully") }
        } catch (e: Exception) {
            Log.e(TAG, "Error providing AvatarRepository", e)
            throw e
        }
    }
} 