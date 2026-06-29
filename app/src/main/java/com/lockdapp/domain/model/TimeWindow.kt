package com.lockdapp.domain.model

import kotlinx.serialization.Serializable

/**
 * A time range within a single day. startMinute < endMinute always;
 * windows crossing midnight must be split into two rules (by design).
 */
@Serializable
data class TimeWindow(
    val startMinute: Int,  // 540  = 09:00
    val endMinute: Int,    // 1020 = 17:00
)
