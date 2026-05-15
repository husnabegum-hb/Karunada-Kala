package com.example.karunada_kala.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.Event
import com.example.karunada_kala.domain.model.Review
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
class StudioDashboardViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myEvents = MutableStateFlow<List<Event>>(emptyList())
    val myEvents: StateFlow<List<Event>> = _myEvents.asStateFlow()

    private val _myReviews = MutableStateFlow<List<Review>>(emptyList())
    val myReviews: StateFlow<List<Review>> = _myReviews.asStateFlow()

    private val _artisan = MutableStateFlow<com.example.karunada_kala.domain.model.Artisan?>(null)
    val artisan: StateFlow<com.example.karunada_kala.domain.model.Artisan?> = _artisan.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: return@launch
            launch {
                val art = dataRepository.getArtisanById(user.id)
                _artisan.value = art
            }
            launch {
                dataRepository.getEventsByArtisan(user.id).collect { _myEvents.value = it }
            }
            launch {
                dataRepository.getReviewsByArtisan(user.id).collect {
                    _myReviews.value = it.sortedByDescending { r -> r.timestamp }
                }
            }
        }
    }
}
