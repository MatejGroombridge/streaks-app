package dev.matejgroombridge.streaks.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.streakDataStore: DataStore<Preferences> by preferencesDataStore(name = "streak_state")

class StreakRepository(private val context: Context) {
    val state: Flow<StreakState> = context.streakDataStore.data.map { prefs ->
        StreakState(
            startEpochDay = prefs[KEY_START_DAY] ?: LocalDate.now().toEpochDay(),
            failureEpochDays = parseLongSet(prefs[KEY_FAILURE_DAYS]),
            blocker = BlockerState(
                blockAllPornSites = prefs[KEY_BLOCK_ALL] ?: false,
                customSites = parseSites(prefs[KEY_CUSTOM_SITES]),
                blockerEnabled = prefs[KEY_BLOCKER_ENABLED] ?: false,
            ),
        )
    }

    suspend fun recordFailure(day: Long = LocalDate.now().toEpochDay()) {
        context.streakDataStore.edit { prefs ->
            val current = parseLongSet(prefs[KEY_FAILURE_DAYS]).toMutableSet()
            current += day
            prefs[KEY_FAILURE_DAYS] = current.sorted().joinToString(",")
        }
    }

    suspend fun setFailure(day: Long, failed: Boolean) {
        context.streakDataStore.edit { prefs ->
            val current = parseLongSet(prefs[KEY_FAILURE_DAYS]).toMutableSet()
            if (failed) current += day else current -= day
            prefs[KEY_FAILURE_DAYS] = current.sorted().joinToString(",")
        }
    }

    suspend fun resetStartDate(day: Long = LocalDate.now().toEpochDay()) {
        context.streakDataStore.edit { prefs ->
            prefs[KEY_START_DAY] = day
            prefs.remove(KEY_FAILURE_DAYS)
        }
    }

    suspend fun setBlockAllPornSites(enabled: Boolean) {
        context.streakDataStore.edit { prefs -> prefs[KEY_BLOCK_ALL] = enabled }
    }

    suspend fun setBlockerEnabled(enabled: Boolean) {
        context.streakDataStore.edit { prefs -> prefs[KEY_BLOCKER_ENABLED] = enabled }
    }

    suspend fun addSite(rawSite: String) {
        val site = normalizeSite(rawSite) ?: return
        context.streakDataStore.edit { prefs ->
            val current = parseSites(prefs[KEY_CUSTOM_SITES]).toMutableList()
            if (current.none { it.equals(site, ignoreCase = true) }) current += site
            prefs[KEY_CUSTOM_SITES] = current.sorted().joinToString("\n")
        }
    }

    suspend fun removeSite(site: String) {
        context.streakDataStore.edit { prefs ->
            val current = parseSites(prefs[KEY_CUSTOM_SITES]).filterNot { it == site }
            prefs[KEY_CUSTOM_SITES] = current.joinToString("\n")
        }
    }

    private fun parseLongSet(raw: String?): Set<Long> = raw
        ?.split(',')
        ?.mapNotNull { it.trim().toLongOrNull() }
        ?.toSet()
        ?: emptySet()

    private fun parseSites(raw: String?): List<String> = raw
        ?.lineSequence()
        ?.mapNotNull(::normalizeSite)
        ?.distinct()
        ?.toList()
        ?: emptyList()

    private fun normalizeSite(raw: String): String? {
        val trimmed = raw.trim().lowercase()
            .removePrefix("https://")
            .removePrefix("http://")
            .removePrefix("www.")
            .substringBefore('/')
            .substringBefore('?')
        return trimmed.takeIf { it.contains('.') && it.length >= 4 }
    }

    private companion object {
        val KEY_START_DAY = longPreferencesKey("start_epoch_day")
        val KEY_FAILURE_DAYS = stringPreferencesKey("failure_epoch_days")
        val KEY_BLOCK_ALL = booleanPreferencesKey("block_all_porn_sites")
        val KEY_CUSTOM_SITES = stringPreferencesKey("custom_sites")
        val KEY_BLOCKER_ENABLED = booleanPreferencesKey("blocker_enabled")
    }
}
