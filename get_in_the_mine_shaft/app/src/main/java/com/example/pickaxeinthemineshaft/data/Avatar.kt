package com.example.pickaxeinthemineshaft.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date
import com.example.pickaxeinthemineshaft.data.model.Mood
import com.example.pickaxeinthemineshaft.data.model.PersonalityType

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val requiredValue: Int,
    val currentValue: Int = 0,
    val isUnlocked: Boolean = false,
    val rewardCoins: Int = 50,
    val rewardExperience: Int = 100
)

@Entity(tableName = "avatars")
@TypeConverters(Converters::class)
data class Avatar(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "Your Avatar",
    val level: Int = 1,
    val experience: Int = 0,
    val coins: Int = 0,
    val mood: Mood = Mood.NEUTRAL,
    val energy: Int = 100,
    val happiness: Int = 100,
    val motivation: Int = 100,
    val lastInteractionTime: Date = Date(),
    val personalityType: PersonalityType = PersonalityType.FRIENDLY,
    val streak: Int = 0,
    val consecutiveTasksDone: Int = 0,
    val consecutiveTasksFailed: Int = 0,
    val totalTasksCompleted: Int = 0,
    val totalTasksFailed: Int = 0,
    val lastTaskCompletedAt: Date? = null,
    val selectedOutfit: String = "default",
    val unlockedOutfits: List<String> = listOf("default"),
    val achievements: List<String> = emptyList()
)