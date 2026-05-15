package com.example.karunada_kala.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.Artisan
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadArtisans()
    }

    private fun loadArtisans() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            dataRepository.getArtisans().collect { artisans ->
                _uiState.value = HomeUiState.Success(artisans)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val filteredList = dataRepository.searchArtisans(query)
            _uiState.value = HomeUiState.Success(filteredList)
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val artisans: List<Artisan>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
