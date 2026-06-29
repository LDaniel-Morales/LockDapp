package com.lockdapp.data.local

import androidx.room.TypeConverter
import com.lockdapp.domain.model.TimeWindow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.DayOfWeek
import java.time.LocalDate

private val json = Json { ignoreUnknownKeys = true }

class LockConverters {

    // ── List<String> ──────────────────────────────────────────────────────────
    @TypeConverter
    fun listStringToJson(value: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun jsonToListString(value: String): List<String> =
        json.decodeFromString(ListSerializer(String.serializer()), value)

    // ── List<Long> ────────────────────────────────────────────────────────────
    @TypeConverter
    fun listLongToJson(value: List<Long>): String =
        json.encodeToString(ListSerializer(Long.serializer()), value)

    @TypeConverter
    fun jsonToListLong(value: String): List<Long> =
        json.decodeFromString(ListSerializer(Long.serializer()), value)

    // ── List<TimeWindow> ──────────────────────────────────────────────────────
    @TypeConverter
    fun listWindowToJson(value: List<TimeWindow>): String =
        json.encodeToString(ListSerializer(TimeWindow.serializer()), value)

    @TypeConverter
    fun jsonToListWindow(value: String): List<TimeWindow> =
        json.decodeFromString(ListSerializer(TimeWindow.serializer()), value)

    // ── Set<DayOfWeek> — stored as list of ISO names ("MONDAY", …) ───────────
    @TypeConverter
    fun setDayOfWeekToJson(value: Set<DayOfWeek>): String =
        json.encodeToString(ListSerializer(String.serializer()), value.map { it.name })

    @TypeConverter
    fun jsonToSetDayOfWeek(value: String): Set<DayOfWeek> =
        json.decodeFromString(ListSerializer(String.serializer()), value)
            .map { DayOfWeek.valueOf(it) }
            .toSet()

    // ── LocalDate? ────────────────────────────────────────────────────────────
    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? =
        value?.let { LocalDate.parse(it) }
}
