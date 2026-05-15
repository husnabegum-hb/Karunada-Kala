package com.example.karunada_kala.domain.repository

import com.example.karunada_kala.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, pass: String): Result<User>
    suspend fun register(email: String, pass: String, name: String, role: String): Result<User>
    suspend fun loginAsGuest(): Result<User>
    suspend fun logout()
}
