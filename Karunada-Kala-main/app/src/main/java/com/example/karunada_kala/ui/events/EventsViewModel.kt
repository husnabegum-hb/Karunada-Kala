package com.example.karunada_kala.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.Event
import com.example.karunada_kala.domain.repository.AuthRepository
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    private val _myBookedEventIds = MutableStateFlow<List<String>>(emptyList())
    val myBookedEventIds: StateFlow<List<String>> = _myBookedEventIds.asStateFlow()

    private val initialFilter: Boolean = savedStateHandle["showOnlyMyBookings"] ?: false
    private val _showOnlyMyBookings = MutableStateFlow(initialFilter)
    val showOnlyMyBookings: StateFlow<Boolean> = _showOnlyMyBookings.asStateFlow()

    init {
        loadEvents()
        loadMyBookings()
    }

    fun setFilterMyBookings(showOnlyMine: Boolean) {
        _showOnlyMyBookings.value = showOnlyMine
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = EventsUiState.Loading
            dataRepository.getEvents().collect { events ->
                _uiState.value = EventsUiState.Success(events)
            }
        }
    }

    private fun loadMyBookings() {
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            if (user != null) {
                dataRepository.getMyBookedEventIds(user.id).collect { ids ->
                    _myBookedEventIds.value = ids
                }
            }
        }
    }

    private val _bookSuccess = MutableStateFlow(false)
    val bookSuccess: StateFlow<Boolean> = _bookSuccess.asStateFlow()

    fun bookEvent(eventId: String) {
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            if (user != null) {
                dataRepository.bookEvent(eventId, user.id)
                _bookSuccess.value = true
            }
        }
    }

    fun resetBookSuccess() {
        _bookSuccess.value = false
    }
}

sealed class EventsUiState {
    object Loading : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val message: String) : EventsUiState()
}
