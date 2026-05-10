package dev.matejgroombridge.streaks.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.matejgroombridge.streaks.data.AppSettings
import dev.matejgroombridge.streaks.data.SettingsRepository
import dev.matejgroombridge.streaks.data.ThemeMode
import dev.matejgroombridge.streaks.data.WeekStart
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val settings: StateFlow<AppSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppSettings(),
    )

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { repository.setThemeMode(mode) }
    fun setAmoled(enabled: Boolean) = viewModelScope.launch { repository.setAmoled(enabled) }
    fun setWeekStart(weekStart: WeekStart) = viewModelScope.launch { repository.setWeekStart(weekStart) }
    fun setSwipeToNavigate(enabled: Boolean) = viewModelScope.launch { repository.setSwipeToNavigate(enabled) }
    fun setDailyCheckReminder(enabled: Boolean) = viewModelScope.launch { repository.setDailyCheckReminder(enabled) }
    fun setZenMode(enabled: Boolean) = viewModelScope.launch { repository.setZenMode(enabled) }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer { SettingsViewModel(SettingsRepository(application.applicationContext)) }
        }
    }
}
