package dev.matejgroombridge.streaks.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[KEY_THEME_MODE]?.let(::parseThemeMode) ?: ThemeMode.System,
            amoled = prefs[KEY_AMOLED] ?: false,
            weekStart = prefs[KEY_WEEK_START]?.let(::parseWeekStart) ?: WeekStart.Default,
            swipeToNavigate = prefs[KEY_SWIPE_TO_NAVIGATE] ?: true,
            dailyCheckReminder = prefs[KEY_DAILY_CHECK_REMINDER] ?: false,
            zenMode = prefs[KEY_ZEN_MODE] ?: false,
            showHabitNames = prefs[KEY_SHOW_HABIT_NAMES] ?: false,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) = context.settingsDataStore.edit { it[KEY_THEME_MODE] = mode.name }
    suspend fun setAmoled(enabled: Boolean) = context.settingsDataStore.edit { it[KEY_AMOLED] = enabled }
    suspend fun setWeekStart(weekStart: WeekStart) = context.settingsDataStore.edit { it[KEY_WEEK_START] = weekStart.name }
    suspend fun setSwipeToNavigate(enabled: Boolean) = context.settingsDataStore.edit { it[KEY_SWIPE_TO_NAVIGATE] = enabled }
    suspend fun setDailyCheckReminder(enabled: Boolean) = context.settingsDataStore.edit { it[KEY_DAILY_CHECK_REMINDER] = enabled }
    suspend fun setZenMode(enabled: Boolean) = context.settingsDataStore.edit { it[KEY_ZEN_MODE] = enabled }
    suspend fun setShowHabitNames(enabled: Boolean) = context.settingsDataStore.edit { it[KEY_SHOW_HABIT_NAMES] = enabled }

    private fun parseThemeMode(raw: String): ThemeMode = runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.System)
    private fun parseWeekStart(raw: String): WeekStart = runCatching { WeekStart.valueOf(raw) }.getOrDefault(WeekStart.Default)

    private companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_AMOLED = booleanPreferencesKey("amoled")
        val KEY_WEEK_START = stringPreferencesKey("week_start")
        val KEY_SWIPE_TO_NAVIGATE = booleanPreferencesKey("swipe_to_navigate")
        val KEY_DAILY_CHECK_REMINDER = booleanPreferencesKey("daily_check_reminder")
        val KEY_ZEN_MODE = booleanPreferencesKey("zen_mode")
        val KEY_SHOW_HABIT_NAMES = booleanPreferencesKey("show_habit_names")
    }
}
