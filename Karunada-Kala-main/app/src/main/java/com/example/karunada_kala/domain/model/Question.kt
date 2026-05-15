package com.example.karunada_kala.domain.model

data class Question(
    val id: String = "",
    val userId: String = "",
    val artFormId: String = "",
    val questionText: String = "",
    val answerText: String? = null,
    val guruId: String? = null
)
