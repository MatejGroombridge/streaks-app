package dev.matejgroombridge.streaks.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.matejgroombridge.streaks.data.HabitSlot
import dev.matejgroombridge.streaks.data.StreakRepository
import dev.matejgroombridge.streaks.data.StreakState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class StreakViewModel(private val repository: StreakRepository) : ViewModel() {
    val state: StateFlow<StreakState> = repository.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StreakState(),
    )

    fun recordFailureToday(slot: HabitSlot) = viewModelScope.launch { repository.recordFailure(slot) }
    fun setFailure(slot: HabitSlot, day: Long, failed: Boolean) = viewModelScope.launch { repository.setFailure(slot, day, failed) }
    fun setDayFailures(day: Long, primaryFailed: Boolean, secondaryFailed: Boolean) =
        viewModelScope.launch { repository.setDayFailures(day, primaryFailed, secondaryFailed) }
    fun resetStartDate() = viewModelScope.launch { repository.resetStartDate(LocalDate.now().toEpochDay()) }
    fun saveHabit(slot: HabitSlot, name: String, iconKey: String, colorKey: String) =
        viewModelScope.launch { repository.saveHabit(slot, name, iconKey, colorKey) }
    fun removeSecondaryHabit() = viewModelScope.launch { repository.removeSecondaryHabit() }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer { StreakViewModel(StreakRepository(application.applicationContext)) }
        }
    }
}
