package com.example.karunada_kala.domain.model

data class Post(
    val id: String = "",
    val artisanId: String = "",
    val artFormId: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val timestamp: Long = 0L,
    val likesCount: Int = 0
)
