package dev.matejgroombridge.streaks.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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

    fun recordFailureToday() = viewModelScope.launch { repository.recordFailure() }
    fun setFailure(day: Long, failed: Boolean) = viewModelScope.launch { repository.setFailure(day, failed) }
    fun resetStartDate() = viewModelScope.launch { repository.resetStartDate(LocalDate.now().toEpochDay()) }
    fun setBlockAllPornSites(enabled: Boolean) = viewModelScope.launch { repository.setBlockAllPornSites(enabled) }
    fun setBlockerEnabled(enabled: Boolean) = viewModelScope.launch { repository.setBlockerEnabled(enabled) }
    fun addSite(site: String) = viewModelScope.launch { repository.addSite(site) }
    fun removeSite(site: String) = viewModelScope.launch { repository.removeSite(site) }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer { StreakViewModel(StreakRepository(application.applicationContext)) }
        }
    }
}
