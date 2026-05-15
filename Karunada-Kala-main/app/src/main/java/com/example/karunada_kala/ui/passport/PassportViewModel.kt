package com.example.karunada_kala.ui.passport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.repository.AuthRepository
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassportViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _stamps = MutableStateFlow<List<String>>(emptyList())
    val stamps: StateFlow<List<String>> = _stamps.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _stamps.value = user?.districtStamps ?: emptyList()
            }
        }
    }

    fun simulateCheckIn() {
        viewModelScope.launch {
            val current = _stamps.value.toMutableList()
            if (!current.contains("Udupi")) {
                current.add("Udupi")
                _stamps.value = current
                dataRepository.checkInPassport("Udupi")
            } else if (!current.contains("Bidar")) {
                current.add("Bidar")
                _stamps.value = current
                dataRepository.checkInPassport("Bidar")
            }
        }
    }
}
