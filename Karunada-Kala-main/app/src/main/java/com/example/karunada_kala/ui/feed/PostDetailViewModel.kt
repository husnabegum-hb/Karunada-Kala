package com.example.karunada_kala.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.karunada_kala.domain.model.Comment
import com.example.karunada_kala.domain.model.Post
import com.example.karunada_kala.domain.repository.AuthRepository
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val postId: String = checkNotNull(savedStateHandle["postId"])

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _post.value = dataRepository.getPostById(postId)
            dataRepository.getCommentsByPost(postId).collect { list ->
                _comments.value = list
            }
        }
    }

    fun toggleLike() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    dataRepository.toggleLike(postId, user.id)
                    // Optimistic update
                    _post.value = _post.value?.copy(likesCount = (_post.value?.likesCount ?: 0) + 1)
                }
            }
        }
    }

    fun addComment(text: String) {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null && text.isNotBlank()) {
                    val comment = Comment(
                        postId = postId,
                        userId = user.id,
                        text = text
                    )
                    dataRepository.addComment(comment)
                }
            }
        }
    }
}
