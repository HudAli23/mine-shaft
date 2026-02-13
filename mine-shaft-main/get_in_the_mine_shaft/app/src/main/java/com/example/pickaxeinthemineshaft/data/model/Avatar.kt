package com.example.pickaxeinthemineshaft.data.model

import java.util.Date

data class Avatar(
    val level: Int = 1,
    val coins: Int = 0,
    val selectedOutfit: String = "default",
    val unlockedOutfits: List<String> = listOf("default"),
    val achievements: List<String> = emptyList(),
    val personalityType: PersonalityType = PersonalityType.FRIENDLY,
    val streak: Int = 0,
    val totalTasksCompleted: Int = 0,
    val lastInteractionTime: Date = Date()
)
