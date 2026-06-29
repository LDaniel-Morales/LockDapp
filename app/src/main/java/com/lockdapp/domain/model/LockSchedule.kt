package com.lockdapp.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

data class LockSchedule(
    val id: Long,
    val name: String,
    val targetGroupIds: List<Long>,
    val windows: List<TimeWindow>,
    val daysOfWeek: Set<DayOfWeek>,
    val validFrom: LocalDate?,   // null = indefinite start
    val validUntil: LocalDate?,  // null = indefinite end
    val enabled: Boolean,
)
