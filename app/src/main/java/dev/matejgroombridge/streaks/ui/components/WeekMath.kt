package dev.matejgroombridge.streaks.ui.components

import dev.matejgroombridge.streaks.data.WeekStart
import java.time.DayOfWeek
import java.time.LocalDate

/** Shared week math from Habit Tracker, adapted to Streaks settings. */
object WeekMath {
    fun weekRange(day: Long, weekStart: WeekStart): Pair<Long, Long> {
        val date = LocalDate.ofEpochDay(day)
        val startDay = when (weekStart) {
            WeekStart.Sunday -> DayOfWeek.SUNDAY
            else -> DayOfWeek.MONDAY
        }
        val daysSinceStart = daysSinceWeekStart(date.dayOfWeek, startDay)
        val start = day - daysSinceStart
        return start to (start + 6)
    }

    private fun daysSinceWeekStart(today: DayOfWeek, weekStart: DayOfWeek): Int {
        val diff = today.value - weekStart.value
        return ((diff % 7) + 7) % 7
    }
}
