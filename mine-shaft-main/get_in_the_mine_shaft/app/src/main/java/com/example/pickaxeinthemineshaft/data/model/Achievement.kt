package com.example.pickaxeinthemineshaft.data.model

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val requiredValue: Int,
    val currentValue: Int,
    val isUnlocked: Boolean,
    val rewardCoins: Int
) 