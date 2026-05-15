package com.example.karunada_kala.ui.artform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.ArtForm
import com.example.karunada_kala.domain.model.Artisan
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val artFormId: String = checkNotNull(savedStateHandle["artFormId"])

    private val _artForm = MutableStateFlow<ArtForm?>(null)
    val artForm: StateFlow<ArtForm?> = _artForm.asStateFlow()

    private val _gurus = MutableStateFlow<List<Artisan>>(emptyList())
    val gurus: StateFlow<List<Artisan>> = _gurus.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _artForm.value = dataRepository.getArtFormById(artFormId)
            dataRepository.getArtisansByArtForm(artFormId).collect { list ->
                _gurus.value = list
            }
        }
    }
}
