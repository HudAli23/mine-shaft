package com.example.pickaxeinthemineshaft.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.example.pickaxeinthemineshaft.data.model.Mood

private const val TAG = "AvatarRepository"

@Singleton
class AvatarRepository @Inject constructor(
    private val avatarDao: AvatarDao
) {
    fun getAvatar(): Flow<Avatar?> = avatarDao.getAvatar()
    
    suspend fun createAvatar(avatar: Avatar): Long = avatarDao.insertAvatar(avatar)
    
    suspend fun updateAvatar(avatar: Avatar) = avatarDao.updateAvatar(avatar)
    
    suspend fun addCoins(avatarId: Long, amount: Int) = avatarDao.addCoins(avatarId, amount)
    
    suspend fun unlockOutfit(avatarId: Long, outfitName: String) {
        try {
            // Get avatar synchronously
            val avatar = getAvatarSync() ?: return
            
            val newOutfits = avatar.unlockedOutfits.toMutableList()
            if (!newOutfits.contains(outfitName)) {
                newOutfits.add(outfitName)
                avatarDao.updateUnlockedOutfits(avatarId, newOutfits)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unlocking outfit", e)
        }
    }
    
    suspend fun unlockAchievement(avatarId: Long, achievementId: String) {
        try {
            // Get avatar synchronously
            val avatar = getAvatarSync() ?: return
            
            val newAchievements = avatar.achievements.toMutableList()
            if (!newAchievements.contains(achievementId)) {
                newAchievements.add(achievementId)
                avatarDao.updateAchievements(avatarId, newAchievements)
                avatarDao.addCoins(avatarId, 50) // Reward for achievement
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unlocking achievement", e)
        }
    }
    
    suspend fun onTaskCompleted(avatarId: Long) {
        try {
            // Get avatar synchronously
            val avatar = getAvatarSync() ?: return
            
            val now = Date()
            val lastCompleted = avatar.lastTaskCompletedAt
            
            // Calculate streak
            val isConsecutive = lastCompleted?.let { last ->
                val diffInMillis = now.time - last.time
                TimeUnit.MILLISECONDS.toDays(diffInMillis) <= 1
            } ?: true
            
            val newStreak = if (isConsecutive) avatar.streak + 1 else 1
            val newConsecutiveTasksDone = avatar.consecutiveTasksDone + 1
            val newTotalTasksCompleted = avatar.totalTasksCompleted + 1
            
            // Update avatar stats
            avatarDao.updateTaskStats(
                avatarId = avatarId,
                streak = newStreak,
                lastTaskCompletedAt = now,
                consecutiveTasksDone = newConsecutiveTasksDone,
                consecutiveTasksFailed = 0, // Reset failed streak
                totalTasksCompleted = newTotalTasksCompleted,
                totalTasksFailed = avatar.totalTasksFailed // Keep existing failed count
            )
            
            // Update mood and energy
            val newMood = when {
                newStreak >= 7 -> Mood.ECSTATIC
                newConsecutiveTasksDone >= 3 -> Mood.HAPPY
                else -> Mood.NEUTRAL
            }
            
            val energyBoost = when {
                newStreak >= 7 -> 20
                newConsecutiveTasksDone >= 3 -> 10
                else -> 5
            }
            
            avatarDao.updateMoodAndEnergy(
                avatarId = avatarId,
                mood = newMood,
                energy = minOf(100, avatar.energy + energyBoost)
            )
            
            // Check for streak achievements
            when (newStreak) {
                7 -> unlockAchievement(avatarId, "WEEK_STREAK")
                30 -> unlockAchievement(avatarId, "MONTH_STREAK")
                100 -> unlockAchievement(avatarId, "HUNDRED_STREAK")
            }
            
            // Award experience and coins
            val baseXp = 50
            val streakBonus = (newStreak / 7) * 25 // Bonus XP for every week of streak
            addCoins(avatarId, 10) // Base coins for completing a task
        } catch (e: Exception) {
            Log.e(TAG, "Error handling task completion", e)
        }
    }
    
    suspend fun onTaskFailed(avatarId: Long) {
        try {
            // Get avatar synchronously
            val avatar = getAvatarSync() ?: return
            
            val newConsecutiveTasksFailed = avatar.consecutiveTasksFailed + 1
            val newTotalTasksFailed = avatar.totalTasksFailed + 1
            
            // Update avatar stats
            avatarDao.updateTaskStats(
                avatarId = avatarId,
                streak = 0, // Reset streak
                lastTaskCompletedAt = avatar.lastTaskCompletedAt,
                consecutiveTasksDone = 0, // Reset success streak
                consecutiveTasksFailed = newConsecutiveTasksFailed,
                totalTasksCompleted = avatar.totalTasksCompleted, // Keep existing total
                totalTasksFailed = newTotalTasksFailed
            )
            
            // Update mood and energy
            val newMood = when {
                newConsecutiveTasksFailed >= 3 -> Mood.SAD
                avatar.energy <= 30 -> Mood.TIRED
                else -> Mood.NEUTRAL
            }
            
            val energyLoss = when {
                newConsecutiveTasksFailed >= 3 -> 20
                newConsecutiveTasksFailed >= 2 -> 15
                else -> 10
            }
            
            avatarDao.updateMoodAndEnergy(
                avatarId = avatarId,
                mood = newMood,
                energy = maxOf(0, avatar.energy - energyLoss)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error handling task failure", e)
        }
    }
    
    // Helper method to get avatar synchronously
    private suspend fun getAvatarSync(): Avatar? {
        return try {
            avatarDao.getAvatar().first()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting avatar synchronously", e)
            null
        }
    }
} 