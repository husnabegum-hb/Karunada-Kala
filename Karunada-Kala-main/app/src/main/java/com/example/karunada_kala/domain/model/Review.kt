package com.example.karunada_kala.domain.model

data class Review(
    val id: String = "",
    val artisanId: String = "",
    val userId: String = "",
    val userName: String = "Anonymous",
    val eventId: String = "",          // optional: which event this reviews
    val rating: Float = 0f,
    val comment: String = "",
    val artisanReply: String? = null,  // artisan can reply to review
    val isVerifiedBuyer: Boolean = false,
    val timestamp: Long = 0L
)
