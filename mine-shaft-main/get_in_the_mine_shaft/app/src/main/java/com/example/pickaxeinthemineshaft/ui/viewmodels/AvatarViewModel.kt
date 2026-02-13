package com.example.pickaxeinthemineshaft.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickaxeinthemineshaft.data.AvatarRepository
import com.example.pickaxeinthemineshaft.data.model.Mood
import com.example.pickaxeinthemineshaft.data.model.PersonalityType
import com.example.pickaxeinthemineshaft.data.model.Achievement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AvatarViewModel @Inject constructor(
    private val avatarRepository: AvatarRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AvatarUiState())
    val uiState: StateFlow<AvatarUiState> = _uiState.asStateFlow()
    
    private val availableOutfits = listOf(
        "default",
        "warrior",
        "mage",
        "rogue",
        "paladin",
        "ninja"
    )
    
    private val availableAchievements = listOf(
        Achievement(
            id = "FIRST_TASK",
            name = "First Step",
            description = "Complete your first task",
            requiredValue = 1,
            currentValue = 0,
            isUnlocked = false,
            rewardCoins = 50
        ),
        Achievement(
            id = "WEEK_STREAK",
            name = "Consistency",
            description = "Maintain a 7-day streak",
            requiredValue = 7,
            currentValue = 0,
            isUnlocked = false,
            rewardCoins = 100
        ),
        Achievement(
            id = "MONTH_STREAK",
            name = "Dedication",
            description = "Maintain a 30-day streak",
            requiredValue = 30,
            currentValue = 0,
            isUnlocked = false,
            rewardCoins = 200
        ),
        Achievement(
            id = "HUNDRED_STREAK",
            name = "Master of Habits",
            description = "Maintain a 100-day streak",
            requiredValue = 100,
            currentValue = 0,
            isUnlocked = false,
            rewardCoins = 500
        ),
        Achievement(
            id = "LEVEL_10",
            name = "Rising Star",
            description = "Reach level 10",
            requiredValue = 10,
            currentValue = 0,
            isUnlocked = false,
            rewardCoins = 150
        ),
        Achievement(
            id = "LEVEL_25",
            name = "Expert",
            description = "Reach level 25",
            requiredValue = 25,
            currentValue = 0,
            isUnlocked = false,
            rewardCoins = 300
        )
    )
    
    init {
        viewModelScope.launch {
            avatarRepository.getAvatar().collect { avatar ->
                // Dummy values for completionRate and tasksCompletedToday (replace with real logic if available)
                val completionRate = 68 // TODO: Replace with real stat
                val tasksCompletedToday = 3 // TODO: Replace with real stat
                val reaction = when {
                    completionRate >= 70 -> "PROUD"
                    completionRate <= 40 -> "SCORNFUL"
                    else -> "ENCOURAGING"
                }
                _uiState.value = AvatarUiState(
                    level = avatar?.level ?: 1,
                    coins = avatar?.coins ?: 0,
                    selectedOutfit = avatar?.selectedOutfit ?: "default",
                    unlockedOutfits = avatar?.unlockedOutfits ?: listOf("default"),
                    unlockedAchievements = avatar?.achievements ?: emptyList(),
                    outfits = availableOutfits,
                    achievements = availableAchievements.map { achievement ->
                        achievement.copy(
                            isUnlocked = avatar?.achievements?.contains(achievement.id) ?: false,
                            currentValue = when (achievement.id) {
                                "FIRST_TASK" -> avatar?.totalTasksCompleted ?: 0
                                "WEEK_STREAK", "MONTH_STREAK", "HUNDRED_STREAK" -> avatar?.streak ?: 0
                                "LEVEL_10", "LEVEL_25" -> avatar?.level ?: 1
                                else -> 0
                            }
                        )
                    },
                    personalityType = avatar?.personalityType ?: PersonalityType.FRIENDLY,
                    streak = avatar?.streak ?: 0,
                    totalTasksCompleted = avatar?.totalTasksCompleted ?: 0,
                    completionRate = completionRate,
                    tasksCompletedToday = tasksCompletedToday,
                    petReaction = reaction
                )
            }
        }
    }
    
    fun selectOutfit(outfit: String) {
        viewModelScope.launch {
            avatarRepository.getAvatar().collect { avatar ->
                avatar?.let {
                    if (it.unlockedOutfits.contains(outfit)) {
                        avatarRepository.updateAvatar(it.copy(selectedOutfit = outfit))
                    }
                }
            }
        }
    }

    fun interactWithPet() {
        viewModelScope.launch {
            avatarRepository.getAvatar().collect { avatar ->
                avatar?.let {
                    val updatedHappiness = (it.happiness + 10).coerceAtMost(100)
                    val updatedMotivation = (it.motivation + 5).coerceAtMost(100)
                    avatarRepository.updateAvatar(it.copy(
                        happiness = updatedHappiness,
                        motivation = updatedMotivation,
                        lastInteractionTime = Date()
                    ))
                }
            }
        }
    }
}

data class AvatarUiState(
    val level: Int = 1,
    val coins: Int = 0,
    val selectedOutfit: String = "default",
    val unlockedOutfits: List<String> = listOf("default"),
    val unlockedAchievements: List<String> = emptyList(),
    val outfits: List<String> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val personalityType: PersonalityType = PersonalityType.FRIENDLY,
    val streak: Int = 0,
    val totalTasksCompleted: Int = 0,
    val completionRate: Int = 0,
    val tasksCompletedToday: Int = 0,
    val petReaction: String = "ENCOURAGING"
)