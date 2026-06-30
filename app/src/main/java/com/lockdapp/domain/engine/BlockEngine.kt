package com.lockdapp.domain.engine

import com.lockdapp.domain.model.AppGroup
import com.lockdapp.domain.model.LockSchedule
import java.time.LocalDateTime

/** Returned by [BlockEngine.blockInfo] when a matching rule is found. */
data class BlockInfo(
    val groupName: String,
    val windowEndMinute: Int,   // minutes since midnight, e.g. 1020 = 17:00
)

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

    fun isBlocked(pkg: String, now: LocalDateTime): Boolean =
        blockInfo(pkg, now) != null

    /** Returns info about the first matching block rule, or null if not blocked. */
    fun blockInfo(pkg: String, now: LocalDateTime): BlockInfo? {
        val today       = now.toLocalDate()
        val minuteOfDay = now.hour * 60 + now.minute
        for (s in schedules) {
            if (!s.enabled) continue
            if (s.validFrom  != null && today.isBefore(s.validFrom))  continue
            if (s.validUntil != null && today.isAfter(s.validUntil))  continue
            if (now.dayOfWeek !in s.daysOfWeek)                        continue
            val window = s.windows.firstOrNull { minuteOfDay in it.startMinute until it.endMinute } ?: continue
            val gid    = s.targetGroupIds.firstOrNull { g -> pkg in packagesOf(g) } ?: continue
            val group  = groups.firstOrNull { it.id == gid } ?: continue
            return BlockInfo(group.name, window.endMinute)
        }
        return null
    }
}
