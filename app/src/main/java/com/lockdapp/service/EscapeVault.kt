package com.lockdapp.service

/**
 * In-memory registry of packages temporarily exempted from blocking after the
 * user completes the escape-valve friction flow (30 s countdown).
 *
 * Lives in the same process as the AccessibilityService; no IPC needed.
 * Exemptions expire automatically after [DEFAULT_DURATION_MS].
 */
object EscapeVault {

    private const val DEFAULT_DURATION_MS = 5 * 60 * 1000L  // 5 minutes

    private val expiry = mutableMapOf<String, Long>()

    fun grant(pkg: String, durationMs: Long = DEFAULT_DURATION_MS) {
        expiry[pkg] = System.currentTimeMillis() + durationMs
    }

    fun isExempt(pkg: String): Boolean {
        val exp = expiry[pkg] ?: return false
        return if (System.currentTimeMillis() < exp) {
            true
        } else {
            expiry.remove(pkg)
            false
        }
    }
}
