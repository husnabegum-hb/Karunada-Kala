package com.example.karunada_kala.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profilePictureUrl: String = "",
    val role: String = "USER", // "USER" or "STUDIO"
    val districtStamps: List<String> = emptyList(),
    val bio: String = ""
)
