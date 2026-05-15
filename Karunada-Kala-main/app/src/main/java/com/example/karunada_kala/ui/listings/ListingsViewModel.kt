package com.example.karunada_kala.ui.listings

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
class ListingsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myEvents = MutableStateFlow<List<Event>>(emptyList())
    val myEvents: StateFlow<List<Event>> = _myEvents.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _myQuestions = MutableStateFlow<List<com.example.karunada_kala.domain.model.Question>>(emptyList())
    val myQuestions: StateFlow<List<com.example.karunada_kala.domain.model.Question>> = _myQuestions.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: return@launch
            
            val artisan = dataRepository.getArtisanById(user.id)
            if (artisan != null) {
                _studioDescription.value = artisan.studioDescription
                _studioImages.value = artisan.studioImages
                _artisanLat.value = artisan.lat
                _artisanLng.value = artisan.lng
                _artisanName.value = artisan.name
                _artisanType.value = artisan.type
                _artisanImageUrl.value = artisan.imageUrl
            }

            launch {
                dataRepository.getEventsByArtisan(user.id).collect { events ->
                    _myEvents.value = events
                }
            }

            launch {
                dataRepository.getQuestions().collect { all ->
                    _myQuestions.value = all.filter { it.guruId == user.id }
                }
            }
        }
    }

    fun answerQuestion(questionId: String, answerText: String) {
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: return@launch
            dataRepository.answerQuestion(questionId, answerText, user.id)
        }
    }

    fun createListing(
        title: String,
        description: String,
        date: String,
        locationName: String,
        artFormId: String,
        imageUriString: String? = null
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            val user = authRepository.currentUser.first() ?: return@launch
            // Upload image first if provided
            val imageUrl = if (!imageUriString.isNullOrBlank()) {
                try {
                    dataRepository.uploadImage(
                        imageUriString,
                        "events/${user.id}/${System.currentTimeMillis()}.jpg"
                    )
                } catch (e: Exception) { "" }
            } else ""
            val event = Event(
                title = title,
                description = description,
                date = date,
                locationName = locationName,
                artisanId = user.id,
                artFormId = artFormId,
                imageUrl = imageUrl,
                status = "Upcoming"
            )
            dataRepository.addEvent(event)
            _isSaving.value = false
            _saveSuccess.value = true
        }
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    fun updateBio(bio: String) {
        viewModelScope.launch {
            val user = authRepository.currentUser.first() ?: return@launch
            dataRepository.updateArtisanBio(user.id, bio)
        }
    }

    // ── Studio profile (images + description) ────────────────────────────────

    private val _studioDescription = MutableStateFlow("")
    val studioDescription: StateFlow<String> = _studioDescription.asStateFlow()

    private val _studioImages = MutableStateFlow<List<String>>(emptyList())
    val studioImages: StateFlow<List<String>> = _studioImages.asStateFlow()

    private val _artisanLat = MutableStateFlow(0.0)
    val artisanLat: StateFlow<Double> = _artisanLat.asStateFlow()

    private val _artisanLng = MutableStateFlow(0.0)
    val artisanLng: StateFlow<Double> = _artisanLng.asStateFlow()

    private val _artisanName = MutableStateFlow("")
    val artisanName: StateFlow<String> = _artisanName.asStateFlow()

    private val _artisanType = MutableStateFlow("")
    val artisanType: StateFlow<String> = _artisanType.asStateFlow()

    private val _artisanImageUrl = MutableStateFlow("")
    val artisanImageUrl: StateFlow<String> = _artisanImageUrl.asStateFlow()

    private val _studioSaveSuccess = MutableStateFlow(false)
    val studioSaveSuccess: StateFlow<Boolean> = _studioSaveSuccess.asStateFlow()

    fun saveStudioProfile(
        description: String,
        newLocalUris: List<String>,
        existingUrls: List<String>,
        lat: Double,
        lng: Double,
        name: String,
        type: String,
        newProfileImageUri: String? = null
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            val user = authRepository.currentUser.first() ?: run {
                _isSaving.value = false
                return@launch
            }

            // Upload profile image if new
            val finalProfileImageUrl = if (!newProfileImageUri.isNullOrBlank() && !newProfileImageUri.startsWith("http")) {
                try {
                    dataRepository.uploadImage(
                        newProfileImageUri,
                        "profiles/${user.id}/banner_${System.currentTimeMillis()}.jpg"
                    )
                } catch (e: Exception) { _artisanImageUrl.value }
            } else _artisanImageUrl.value

            // Upload any new local URIs to Storage
            val uploadedUrls = newLocalUris.mapNotNull { uri ->
                try {
                    dataRepository.uploadImage(
                        uri,
                        "studio/${user.id}/${System.currentTimeMillis()}_${uri.hashCode()}.jpg"
                    )
                } catch (e: Exception) { null }
            }
            val allUrls = existingUrls + uploadedUrls
            
            dataRepository.updateStudioProfile(user.id, description, allUrls, lat, lng, name, type, finalProfileImageUrl)
            
            _studioDescription.value = description
            _studioImages.value = allUrls
            _artisanLat.value = lat
            _artisanLng.value = lng
            _artisanName.value = name
            _artisanType.value = type
            _artisanImageUrl.value = finalProfileImageUrl
            _isSaving.value = false
            _studioSaveSuccess.value = true
        }
    }

    fun resetStudioSaveSuccess() { _studioSaveSuccess.value = false }
}
