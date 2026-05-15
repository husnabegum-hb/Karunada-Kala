package com.example.karunada_kala.data.repository

import com.example.karunada_kala.domain.model.User
import com.example.karunada_kala.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MockAuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser

    override suspend fun login(email: String, pass: String): Result<User> {
        delay(1000)
        if (email.isNotBlank() && pass.isNotBlank()) {
            val user = User(id = "user1", name = "Test User", email = email)
            _currentUser.value = user
            return Result.success(user)
        }
        return Result.failure(Exception("Invalid credentials"))
    }

    override suspend fun register(email: String, pass: String, name: String, role: String): Result<User> {
        delay(1000)
        val user = User(id = "user1", name = name, email = email, role = role)
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun loginAsGuest(): Result<User> {
        delay(500)
        val user = User(id = "guest", name = "Guest User", role = "USER")
        _currentUser.value = user
        return Result.success(user)
    }

    override suspend fun logout() {
        delay(500)
        _currentUser.value = null
    }
}
