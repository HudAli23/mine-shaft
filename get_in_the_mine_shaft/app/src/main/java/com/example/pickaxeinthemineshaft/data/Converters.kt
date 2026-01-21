package com.example.pickaxeinthemineshaft.data

import androidx.room.TypeConverter
import com.example.pickaxeinthemineshaft.data.model.Mood
import com.example.pickaxeinthemineshaft.data.model.PersonalityType
import java.util.Date
import java.time.DayOfWeek
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDate(value: Date?): Long? = value?.time

    @TypeConverter
    fun toDate(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun fromMood(value: Mood): String = value.name

    @TypeConverter
    fun toMood(value: String): Mood = Mood.valueOf(value)

    @TypeConverter
    fun fromPersonalityType(value: PersonalityType): String = value.name

    @TypeConverter
    fun toPersonalityType(value: String): PersonalityType = PersonalityType.valueOf(value)

    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString(",")

    @TypeConverter
    fun toStringList(data: String): List<String> = if (data.isEmpty()) emptyList() else data.split(",")

    @TypeConverter
    fun fromDayOfWeekSet(value: Set<DayOfWeek>?): String = gson.toJson(value)

    @TypeConverter
    fun toDayOfWeekSet(value: String?): Set<DayOfWeek> {
        if (value == null) return emptySet()
        val type = object : TypeToken<Set<DayOfWeek>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromTaskFrequency(value: TaskFrequency): String = value.name

    @TypeConverter
    fun toTaskFrequency(value: String): TaskFrequency = TaskFrequency.valueOf(value)
}