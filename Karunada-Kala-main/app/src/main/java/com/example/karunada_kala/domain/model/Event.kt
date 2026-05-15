package com.example.karunada_kala.domain.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val locationName: String = "",
    val imageUrl: String = "",
    val artisanId: String = "",
    val artFormId: String = "",
    val status: String = "Upcoming",
    val bookingsCount: Int = 0
)
