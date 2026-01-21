package com.example.pickaxeinthemineshaft.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date
import com.example.pickaxeinthemineshaft.data.model.Mood

@Dao
interface AvatarDao {
    @Query("SELECT * FROM avatars LIMIT 1")
    fun getAvatar(): Flow<Avatar?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatar(avatar: Avatar): Long

    @Update
    suspend fun updateAvatar(avatar: Avatar)

    @Query("UPDATE avatars SET level = :level WHERE id = :avatarId")
    suspend fun updateLevel(avatarId: Long, level: Int)

    @Query("UPDATE avatars SET coins = coins + :amount WHERE id = :avatarId")
    suspend fun addCoins(avatarId: Long, amount: Int)

    @Query("UPDATE avatars SET unlockedOutfits = :outfits WHERE id = :avatarId")
    suspend fun updateUnlockedOutfits(avatarId: Long, outfits: List<String>)

    @Query("UPDATE avatars SET achievements = :achievements WHERE id = :avatarId")
    suspend fun updateAchievements(avatarId: Long, achievements: List<String>)

    @Query("""
        UPDATE avatars SET 
        streak = :streak,
        lastTaskCompletedAt = :lastTaskCompletedAt,
        consecutiveTasksDone = :consecutiveTasksDone,
        consecutiveTasksFailed = :consecutiveTasksFailed,
        totalTasksCompleted = :totalTasksCompleted,
        totalTasksFailed = :totalTasksFailed
        WHERE id = :avatarId
    """)
    suspend fun updateTaskStats(
        avatarId: Long,
        streak: Int,
        lastTaskCompletedAt: Date?,
        consecutiveTasksDone: Int,
        consecutiveTasksFailed: Int,
        totalTasksCompleted: Int,
        totalTasksFailed: Int
    )

    @Query("""
        UPDATE avatars SET 
        mood = :mood,
        energy = :energy
        WHERE id = :avatarId
    """)
    suspend fun updateMoodAndEnergy(
        avatarId: Long,
        mood: Mood,
        energy: Int
    )
} 