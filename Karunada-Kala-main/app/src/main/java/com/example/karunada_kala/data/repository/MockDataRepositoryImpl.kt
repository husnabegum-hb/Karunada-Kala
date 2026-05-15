package com.example.karunada_kala.data.repository

import com.example.karunada_kala.data.MockData
import com.example.karunada_kala.domain.model.*
import com.example.karunada_kala.domain.repository.DataRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockDataRepositoryImpl @Inject constructor() : DataRepository {
    
    private val questions = MockData.mockQuestions.toMutableList()
    private val reviews = MockData.mockReviews.toMutableList()
    private val comments = mutableListOf<Comment>()
    private val posts = MockData.mockPosts.toMutableList()

    override fun getArtisans(): Flow<List<Artisan>> = flow {
        delay(1000)
        emit(MockData.mockArtisans.reversed())
    }

    override fun getEvents(): Flow<List<Event>> = flow {
        delay(1000)
        emit(MockData.mockEvents)
    }

    override suspend fun searchArtisans(query: String): List<Artisan> {
        delay(500)
        if (query.isBlank()) return MockData.mockArtisans
        return MockData.mockArtisans.filter {
            it.name.contains(query, ignoreCase = true) || it.type.contains(query, ignoreCase = true)
        }
    }

    override fun getArtForms(): Flow<List<ArtForm>> = flow {
        delay(800)
        emit(MockData.mockArtForms)
    }

    override fun getPosts(): Flow<List<Post>> = flow {
        delay(1000)
        emit(posts)
    }

    override fun getQuestions(): Flow<List<Question>> = flow {
        delay(1000)
        emit(questions)
    }

    override fun getReviews(): Flow<List<Review>> = flow {
        delay(1000)
        emit(reviews)
    }

    override suspend fun getArtisanById(artisanId: String): Artisan? {
        delay(500)
        return MockData.mockArtisans.find { it.id == artisanId }
    }

    override suspend fun getArtFormById(artFormId: String): ArtForm? {
        delay(500)
        return MockData.mockArtForms.find { it.id == artFormId }
    }

    override fun getPostsByArtisan(artisanId: String): Flow<List<Post>> = flow {
        delay(500)
        emit(posts.filter { it.artisanId == artisanId })
    }

    override fun getEventsByArtisan(artisanId: String): Flow<List<Event>> = flow {
        delay(500)
        emit(MockData.mockEvents.filter { it.artisanId == artisanId })
    }

    override fun getReviewsByArtisan(artisanId: String): Flow<List<Review>> = flow {
        delay(500)
        emit(reviews.filter { it.artisanId == artisanId })
    }

    override fun getArtisansByArtForm(artFormId: String): Flow<List<Artisan>> = flow {
        delay(500)
        emit(MockData.mockArtisans.filter { it.artFormIds.contains(artFormId) })
    }

    override suspend fun addQuestion(question: Question) {
        delay(500)
        questions.add(question)
    }

    override suspend fun addReview(review: Review) {
        delay(500)
        reviews.add(review)
    }

    override suspend fun checkInPassport(districtName: String) {
        delay(800)
    }

    // Phase 6 interaction stubs
    override suspend fun bookEvent(eventId: String, userId: String) {
        delay(500)
    }

    override suspend fun toggleLike(postId: String, userId: String) {
        val idx = posts.indexOfFirst { it.id == postId }
        if (idx >= 0) {
            posts[idx] = posts[idx].copy(likesCount = posts[idx].likesCount + 1)
        }
    }

    override suspend fun addComment(comment: Comment) {
        comments.add(comment)
    }

    override fun getCommentsByPost(postId: String): Flow<List<Comment>> = flow {
        emit(comments.filter { it.postId == postId })
    }

    override suspend fun getPostById(postId: String): Post? {
        return posts.find { it.id == postId }
    }

    override suspend fun seedDatabase() {
        // No-op for mock — data is already in-memory
    }

    override suspend fun answerQuestion(questionId: String, answerText: String, guruId: String) {
        val idx = questions.indexOfFirst { it.id == questionId }
        if (idx >= 0) questions[idx] = questions[idx].copy(answerText = answerText, guruId = guruId)
    }

    override suspend fun addEvent(event: Event) {
        // For mock just ignore — events list is val
    }

    override suspend fun updateArtisanBio(artisanId: String, bio: String) {
        // No-op for mock
    }

    override suspend fun replyToReview(reviewId: String, replyText: String) {
        val idx = reviews.indexOfFirst { it.id == reviewId }
        if (idx >= 0) reviews[idx] = reviews[idx].copy(artisanReply = replyText)
    }

    override fun getReviewsByEvent(eventId: String): Flow<List<Review>> = flow {
        emit(reviews.filter { it.eventId == eventId })
    }

    override suspend fun uploadImage(localUriString: String, storagePath: String): String {
        return "https://picsum.photos/seed/${storagePath.hashCode()}/800/600"
    }

    override suspend fun updateStudioProfile(
        artisanId: String,
        description: String,
        imageUrls: List<String>,
        lat: Double,
        lng: Double,
        name: String,
        type: String,
        imageUrl: String
    ) {
        // No-op for mock
    }

    override suspend fun clearDatabase() {
        // No-op for mock
    }

    override suspend fun reseedDatabase() {
        // No-op for mock
    }

    override fun getMyBookedEventIds(userId: String): Flow<List<String>> = kotlinx.coroutines.flow.flowOf(emptyList())
}
