package com.example.karunada_kala.domain.model

data class Booking(
    val id: String = "",
    val userId: String = "",
    val eventId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
