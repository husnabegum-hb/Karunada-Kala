package com.example.karunada_kala.ui.auth

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isKannadaMode = MutableStateFlow(false)
    val isKannadaMode: StateFlow<Boolean> = _isKannadaMode.asStateFlow()

    fun setKannadaMode(isKannada: Boolean) {
        _isKannadaMode.value = isKannada
        val languageTag = if (isKannada) "kn" else "en"
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageTag)
        )
    }

    val userRole: StateFlow<String?> = authRepository.currentUser
        .map { it?.role }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUser: StateFlow<com.example.karunada_kala.domain.model.User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Tri-state: Loading (initial) → Authenticated | Unauthenticated
    // This prevents the race condition where null initial value triggers redirects
    val authLoadState: StateFlow<AuthLoadState> = authRepository.currentUser
        .map { user ->
            if (user != null) AuthLoadState.Authenticated(user)
            else AuthLoadState.Unauthenticated
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthLoadState.Loading)

    /** Call this when arriving at AuthScreen to clear any stale Success state. */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Idle
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, pass)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login Failed")
            }
        }
    }

    fun register(email: String, pass: String, name: String, isStudio: Boolean) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val role = if (isStudio) "STUDIO" else "USER"
            val result = authRepository.register(email, pass, name, role)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration Failed")
            }
        }
    }

    fun loginAsGuest() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginAsGuest()
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Guest Login Failed")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class AuthLoadState {
    /** Initial state while the Firebase auth state listener hasn't fired yet. */
    object Loading : AuthLoadState()
    /** Firebase confirmed a logged-in user. */
    data class Authenticated(val user: com.example.karunada_kala.domain.model.User) : AuthLoadState()
    /** Firebase confirmed no user is signed in. */
    object Unauthenticated : AuthLoadState()
}
