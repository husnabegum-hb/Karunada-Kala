package com.example.karunada_kala.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.Artisan
import com.example.karunada_kala.domain.model.Event
import com.example.karunada_kala.domain.model.Post
import com.example.karunada_kala.domain.model.Review
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val artisanId: String = checkNotNull(savedStateHandle["artisanId"])

    private val _artisan = MutableStateFlow<Artisan?>(null)
    val artisan: StateFlow<Artisan?> = _artisan.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _questions = MutableStateFlow<List<com.example.karunada_kala.domain.model.Question>>(emptyList())
    val questions: StateFlow<List<com.example.karunada_kala.domain.model.Question>> = _questions.asStateFlow()

    val currentUserId: String? = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _artisan.value = dataRepository.getArtisanById(artisanId)
            
            launch {
                dataRepository.getPostsByArtisan(artisanId).collect { _posts.value = it }
            }
            launch {
                dataRepository.getEventsByArtisan(artisanId).collect { _events.value = it }
            }
            launch {
                dataRepository.getReviewsByArtisan(artisanId).collect { _reviews.value = it }
            }
            launch {
                dataRepository.getQuestions().collect { all ->
                    _questions.value = all.filter { it.guruId == artisanId }
                }
            }
        }
    }

    fun askQuestion(text: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            dataRepository.addQuestion(
                com.example.karunada_kala.domain.model.Question(
                    userId = uid,
                    artFormId = _artisan.value?.artFormIds?.firstOrNull() ?: "",
                    questionText = text,
                    guruId = artisanId
                )
            )
        }
    }

    fun bookEvent(eventId: String) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            dataRepository.bookEvent(eventId, uid)
        }
    }
}
