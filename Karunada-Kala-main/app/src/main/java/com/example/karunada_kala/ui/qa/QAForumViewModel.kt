package com.example.karunada_kala.ui.qa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.Question
import com.example.karunada_kala.domain.repository.AuthRepository
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QAForumViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting.asStateFlow()

    init {
        viewModelScope.launch {
            dataRepository.getQuestions().collect { list ->
                _questions.value = list
            }
        }
    }

    fun askQuestion(questionText: String, artFormId: String) {
        viewModelScope.launch {
            _isPosting.value = true
            val user = authRepository.currentUser.first() ?: return@launch
            val question = Question(
                userId = user.id,
                artFormId = artFormId,
                questionText = questionText
            )
            dataRepository.addQuestion(question)
            _isPosting.value = false
        }
    }

    fun answerQuestion(questionId: String, answerText: String) {
        viewModelScope.launch {
            _isPosting.value = true
            val user = authRepository.currentUser.first() ?: return@launch
            dataRepository.answerQuestion(questionId, answerText, user.id)
            _isPosting.value = false
        }
    }
}
