package com.lockdapp.domain.engine

import com.lockdapp.domain.model.AppGroup
import com.lockdapp.domain.model.LockSchedule
import java.time.LocalDateTime

/**
 * Pure, Android-free block evaluation logic.
 * Inject schedules and groups; call isBlocked() at any time.
 *
 * Block-wins: any matching schedule → blocked. No exceptions in v1.
 */
class BlockEngine(
    private val schedules: List<LockSchedule>,
    private val groups: List<AppGroup>,
) {
    private fun packagesOf(groupId: Long): Set<String> =
        groups.firstOrNull { it.id == groupId }?.packages?.toSet() ?: emptySet()

    fun isBlocked(pkg: String, now: LocalDateTime): Boolean {
        val today       = now.toLocalDate()
        val minuteOfDay = now.hour * 60 + now.minute
        return schedules.any { s ->
            s.enabled &&
            (s.validFrom  == null || !today.isBefore(s.validFrom)) &&
            (s.validUntil == null || !today.isAfter(s.validUntil)) &&
            now.dayOfWeek in s.daysOfWeek &&
            s.windows.any { minuteOfDay in it.startMinute until it.endMinute } &&
            s.targetGroupIds.any { gid -> pkg in packagesOf(gid) }
        }
    }
}
