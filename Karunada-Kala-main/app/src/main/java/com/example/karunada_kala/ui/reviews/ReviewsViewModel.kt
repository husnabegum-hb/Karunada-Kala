package com.example.karunada_kala.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class ReviewsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadReviewsForArtisan(artisanId: String) {
        viewModelScope.launch {
            dataRepository.getReviewsByArtisan(artisanId).collect {
                _reviews.value = it.sortedByDescending { r -> r.timestamp }
            }
        }
    }

    fun submitReview(artisanId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            _isSaving.value = true
            val user = authRepository.currentUser.first() ?: return@launch
            val review = Review(
                artisanId = artisanId,
                userId = user.id,
                userName = user.name.ifBlank { "Anonymous" },
                rating = rating,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            dataRepository.addReview(review)
            _isSaving.value = false
        }
    }

    fun replyToReview(reviewId: String, replyText: String) {
        viewModelScope.launch {
            _isSaving.value = true
            dataRepository.replyToReview(reviewId, replyText)
            _isSaving.value = false
        }
    }
}
