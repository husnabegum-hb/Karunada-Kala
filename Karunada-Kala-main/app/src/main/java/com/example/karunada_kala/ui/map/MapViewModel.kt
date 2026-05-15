package com.example.karunada_kala.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.Artisan
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _artisans = MutableStateFlow<List<Artisan>>(emptyList())
    
    private val _filter = MutableStateFlow(MapFilter.ALL)
    val filter: StateFlow<MapFilter> = _filter.asStateFlow()

    val filteredArtisans: StateFlow<List<Artisan>> = combine(_artisans, _filter) { artisans, filter ->
        when (filter) {
            MapFilter.ALL -> artisans
            MapFilter.MAKERS -> artisans.filter { !it.isPerformer }
            MapFilter.PERFORMERS -> artisans.filter { it.isPerformer }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadArtisans()
    }

    private fun loadArtisans() {
        viewModelScope.launch {
            dataRepository.getArtisans().collect { list ->
                _artisans.value = list
            }
        }
    }

    fun setFilter(filter: MapFilter) {
        _filter.value = filter
    }
}

enum class MapFilter { ALL, MAKERS, PERFORMERS }
