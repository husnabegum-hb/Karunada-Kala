package com.example.karunada_kala.domain.repository

import com.example.karunada_kala.domain.model.*
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun getArtisans(): Flow<List<Artisan>>
    fun getEvents(): Flow<List<Event>>
    suspend fun searchArtisans(query: String): List<Artisan>

    // New additions for Phase 2
    fun getArtForms(): Flow<List<ArtForm>>
    fun getPosts(): Flow<List<Post>>
    fun getQuestions(): Flow<List<Question>>
    fun getReviews(): Flow<List<Review>>
    
    // Deep Link specific
    suspend fun getArtisanById(artisanId: String): Artisan?
    suspend fun getArtFormById(artFormId: String): ArtForm?
    fun getPostsByArtisan(artisanId: String): Flow<List<Post>>
    fun getEventsByArtisan(artisanId: String): Flow<List<Event>>
    fun getReviewsByArtisan(artisanId: String): Flow<List<Review>>
    fun getArtisansByArtForm(artFormId: String): Flow<List<Artisan>>
    
    suspend fun addQuestion(question: Question)
    suspend fun addReview(review: Review)
    suspend fun checkInPassport(districtName: String)

    // Phase 6 Interactions
    suspend fun bookEvent(eventId: String, userId: String)
    suspend fun toggleLike(postId: String, userId: String)
    suspend fun addComment(comment: Comment)
    fun getCommentsByPost(postId: String): Flow<List<Comment>>
    suspend fun getPostById(postId: String): Post?
    suspend fun seedDatabase()

    // Artisan / Studio operations
    suspend fun answerQuestion(questionId: String, answerText: String, guruId: String)
    suspend fun addEvent(event: Event)
    suspend fun updateArtisanBio(artisanId: String, bio: String)
    suspend fun replyToReview(reviewId: String, replyText: String)
    fun getReviewsByEvent(eventId: String): Flow<List<Review>>
    suspend fun uploadImage(localUriString: String, storagePath: String): String
    suspend fun updateStudioProfile(artisanId: String, description: String, imageUrls: List<String>, lat: Double, lng: Double, name: String, type: String, imageUrl: String)
    suspend fun clearDatabase()
    suspend fun reseedDatabase()
    fun getMyBookedEventIds(userId: String): Flow<List<String>>
}
