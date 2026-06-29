package com.lockdapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lockdapp.domain.model.LockSchedule
import com.lockdapp.domain.model.TimeWindow
import java.time.DayOfWeek
import java.time.LocalDate

@Entity(tableName = "lock_schedules")
data class LockScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetGroupIds: List<Long>,   // TypeConverter → JSON
    val windows: List<TimeWindow>,    // TypeConverter → JSON
    val daysOfWeek: Set<DayOfWeek>,   // TypeConverter → JSON
    val validFrom: LocalDate?,        // TypeConverter → ISO string
    val validUntil: LocalDate?,       // TypeConverter → ISO string
    val enabled: Boolean,
)

fun LockScheduleEntity.toDomain() = LockSchedule(
    id, name, targetGroupIds, windows, daysOfWeek, validFrom, validUntil, enabled
)

fun LockSchedule.toEntity() = LockScheduleEntity(
    id, name, targetGroupIds, windows, daysOfWeek, validFrom, validUntil, enabled
)
