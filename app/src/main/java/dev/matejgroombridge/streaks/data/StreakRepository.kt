package dev.matejgroombridge.streaks.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private val Context.streakDataStore: DataStore<Preferences> by preferencesDataStore(name = "streak_state")

class StreakRepository(private val context: Context) {
    // The single-habit version never saved its start date, so it silently became
    // "today" on every launch. Falling back to the day the app was installed
    // recovers when tracking really began for existing users.
    @Suppress("DEPRECATION")
    private val installEpochDay: Long by lazy {
        runCatching {
            val installedAt = context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
            Instant.ofEpochMilli(installedAt).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay()
        }.getOrElse { LocalDate.now().toEpochDay() }
    }

    val state: Flow<StreakState> = context.streakDataStore.data.map { prefs ->
        StreakState(
            primary = readHabit(prefs, PRIMARY),
            // The secondary habit exists only once the user has named one.
            secondary = prefs[SECONDARY.name]?.let { readHabit(prefs, SECONDARY) },
        )
    }

    suspend fun recordFailure(slot: HabitSlot, day: Long = LocalDate.now().toEpochDay()) {
        setFailure(slot, day, failed = true)
    }

    suspend fun setFailure(slot: HabitSlot, day: Long, failed: Boolean) {
        context.streakDataStore.edit { prefs -> writeFailure(prefs, keysFor(slot), day, failed) }
    }

    /** Sets both habits for [day] in one write, so the UI never sees a half-applied change. */
    suspend fun setDayFailures(day: Long, primaryFailed: Boolean, secondaryFailed: Boolean) {
        context.streakDataStore.edit { prefs ->
            writeFailure(prefs, PRIMARY, day, primaryFailed)
            if (prefs[SECONDARY.name] != null) writeFailure(prefs, SECONDARY, day, secondaryFailed)
        }
    }

    private fun writeFailure(prefs: MutablePreferences, keys: HabitKeys, day: Long, failed: Boolean) {
        val current = parseLongSet(prefs[keys.failureDays]).toMutableSet()
        if (failed) current += day else current -= day
        prefs[keys.failureDays] = current.sorted().joinToString(",")
    }

    suspend fun resetStartDate(day: Long = LocalDate.now().toEpochDay()) {
        context.streakDataStore.edit { prefs ->
            prefs[PRIMARY.startDay] = day
            prefs.remove(PRIMARY.failureDays)
        }
    }

    /** Creates or edits the habit in [slot]. Editing never touches its history. */
    suspend fun saveHabit(slot: HabitSlot, name: String, iconKey: String, colorKey: String) {
        val trimmed = name.trim().take(BadHabit.MAX_NAME_LENGTH)
        if (trimmed.isEmpty()) return
        val keys = keysFor(slot)
        context.streakDataStore.edit { prefs ->
            prefs[keys.name] = trimmed
            prefs[keys.icon] = iconKey
            prefs[keys.color] = colorKey
            // A newly added secondary habit starts its streak on the day it was created.
            if (slot == HabitSlot.Secondary && prefs[keys.startDay] == null) {
                prefs[keys.startDay] = LocalDate.now().toEpochDay()
            }
        }
    }

    suspend fun removeSecondaryHabit() {
        context.streakDataStore.edit { prefs -> SECONDARY.removeAll(prefs) }
    }

    private fun readHabit(prefs: Preferences, keys: HabitKeys): BadHabit {
        val failures = parseLongSet(prefs[keys.failureDays])
        val recordedStart = prefs[keys.startDay] ?: installEpochDay
        return BadHabit(
            name = prefs[keys.name] ?: BadHabit.DEFAULT_NAME,
            iconKey = prefs[keys.icon] ?: BadHabit.DEFAULT_ICON_KEY,
            colorKey = prefs[keys.color] ?: BadHabit.DEFAULT_COLOR_KEY,
            // A failure logged before the start (e.g. from the past-week view) means
            // tracking began earlier, and every stat must count from the same day.
            startEpochDay = minOf(recordedStart, failures.minOrNull() ?: recordedStart),
            failureEpochDays = failures,
        )
    }

    private fun keysFor(slot: HabitSlot): HabitKeys = when (slot) {
        HabitSlot.Primary -> PRIMARY
        HabitSlot.Secondary -> SECONDARY
    }

    private fun parseLongSet(raw: String?): Set<Long> = raw
        ?.split(',')
        ?.mapNotNull { it.trim().toLongOrNull() }
        ?.toSet()
        ?: emptySet()

    private class HabitKeys(
        val name: Preferences.Key<String>,
        val icon: Preferences.Key<String>,
        val color: Preferences.Key<String>,
        val startDay: Preferences.Key<Long>,
        val failureDays: Preferences.Key<String>,
    ) {
        fun removeAll(prefs: MutablePreferences) {
            prefs.remove(name)
            prefs.remove(icon)
            prefs.remove(color)
            prefs.remove(startDay)
            prefs.remove(failureDays)
        }
    }

    private companion object {
        val PRIMARY = HabitKeys(
            name = stringPreferencesKey("primary_habit_name"),
            icon = stringPreferencesKey("primary_habit_icon"),
            color = stringPreferencesKey("primary_habit_color"),
            // Keys from the single-habit version, kept so existing history loads unchanged.
            startDay = longPreferencesKey("start_epoch_day"),
            failureDays = stringPreferencesKey("failure_epoch_days"),
        )
        val SECONDARY = HabitKeys(
            name = stringPreferencesKey("secondary_habit_name"),
            icon = stringPreferencesKey("secondary_habit_icon"),
            color = stringPreferencesKey("secondary_habit_color"),
            startDay = longPreferencesKey("secondary_start_epoch_day"),
            failureDays = stringPreferencesKey("secondary_failure_epoch_days"),
        )
    }
}
