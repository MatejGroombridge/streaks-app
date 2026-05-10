package dev.matejgroombridge.streaks.data

import java.time.LocalDate

/** One immutable snapshot of all app data persisted by StreakRepository. */
data class StreakState(
    val startEpochDay: Long = LocalDate.now().toEpochDay(),
    val failureEpochDays: Set<Long> = emptySet(),
    val blocker: BlockerState = BlockerState(),
) {
    fun currentStreakDays(todayEpochDay: Long = LocalDate.now().toEpochDay()): Long {
        val lastFailure = failureEpochDays.filter { it <= todayEpochDay }.maxOrNull()
        val baseline = lastFailure ?: (startEpochDay - 1)
        return (todayEpochDay - baseline).coerceAtLeast(0)
    }
}

data class BlockerState(
    val blockAllPornSites: Boolean = false,
    val customSites: List<String> = emptyList(),
    val blockerEnabled: Boolean = false,
)

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val amoled: Boolean = false,
    val weekStart: WeekStart = WeekStart.Default,
    val swipeToNavigate: Boolean = true,
    val dailyCheckReminder: Boolean = false,
    val zenMode: Boolean = false,
)

enum class ThemeMode { System, Light, Dark }

enum class WeekStart(val label: String) {
    Default("System"),
    Monday("Monday"),
    Sunday("Sunday"),
}
