package com.example.karunada_kala.domain.model

data class Artisan(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val bio: String = "",
    val imageUrl: String = "",
    val isPerformer: Boolean = false,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val phone: String = "",
    val artFormIds: List<String> = emptyList(),
    // Studio showcase fields
    val studioDescription: String = "",
    val studioImages: List<String> = emptyList(),
    val createdAt: Long = 0L
)
