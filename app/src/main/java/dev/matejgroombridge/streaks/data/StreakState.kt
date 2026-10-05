package dev.matejgroombridge.streaks.data

import java.time.LocalDate

/** One immutable snapshot of all app data persisted by StreakRepository. */
data class StreakState(
    val primary: BadHabit = BadHabit(),
    val secondary: BadHabit? = null,
) {
    fun habit(slot: HabitSlot): BadHabit? = when (slot) {
        HabitSlot.Primary -> primary
        HabitSlot.Secondary -> secondary
    }
}

/**
 * A habit the user is trying to stop. Icon and colour are stored as string keys
 * into the UI catalogues so future catalogue edits never invalidate saved data.
 */
data class BadHabit(
    val name: String = DEFAULT_NAME,
    val iconKey: String = DEFAULT_ICON_KEY,
    val colorKey: String = DEFAULT_COLOR_KEY,
    val startEpochDay: Long = LocalDate.now().toEpochDay(),
    val failureEpochDays: Set<Long> = emptySet(),
) {
    fun currentStreakDays(todayEpochDay: Long = LocalDate.now().toEpochDay()): Long {
        val lastFailure = failureEpochDays.filter { it <= todayEpochDay }.maxOrNull()
        val baseline = lastFailure ?: (startEpochDay - 1)
        return (todayEpochDay - baseline).coerceAtLeast(0)
    }

    companion object {
        // The app only tracked porn before habits became editable, so these
        // defaults reproduce exactly what existing users already see.
        const val DEFAULT_NAME = "Porn"
        const val DEFAULT_ICON_KEY = "fire"
        const val DEFAULT_COLOR_KEY = "orange"
        const val MAX_NAME_LENGTH = 40
    }
}

enum class HabitSlot { Primary, Secondary }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val amoled: Boolean = false,
    val weekStart: WeekStart = WeekStart.Default,
    val swipeToNavigate: Boolean = true,
    val dailyCheckReminder: Boolean = false,
    val zenMode: Boolean = false,
    val showHabitNames: Boolean = false,
)

enum class ThemeMode { System, Light, Dark }

enum class WeekStart(val label: String) {
    Default("System"),
    Monday("Monday"),
    Sunday("Sunday"),
}
