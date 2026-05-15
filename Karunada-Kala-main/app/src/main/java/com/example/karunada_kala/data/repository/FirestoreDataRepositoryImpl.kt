package com.example.karunada_kala.data.repository

import com.example.karunada_kala.domain.model.*
import com.example.karunada_kala.domain.repository.DataRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.firestore.Query
import javax.inject.Inject

class FirestoreDataRepositoryImpl @Inject constructor() : DataRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun getArtisans(): Flow<List<Artisan>> = callbackFlow {
        val listener = db.collection("artisans")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Artisan::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list.sortedByDescending { it.createdAt })
            }
        awaitClose { listener.remove() }
    }

    override fun getEvents(): Flow<List<Event>> = callbackFlow {
        val listener = db.collection("events").addSnapshotListener { snapshot, _ ->
            val list = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java) } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun searchArtisans(query: String): List<Artisan> {
        val snapshot = db.collection("artisans").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Artisan::class.java)?.copy(id = doc.id)
        }.filter { it.name.contains(query, ignoreCase = true) || it.type.contains(query, ignoreCase = true) }
    }

    override fun getArtForms(): Flow<List<ArtForm>> = callbackFlow {
        val listener = db.collection("artForms").addSnapshotListener { snapshot, _ ->
            val list = snapshot?.documents?.mapNotNull { it.toObject(ArtForm::class.java) } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    override fun getPosts(): Flow<List<Post>> = callbackFlow {
        val listener = db.collection("posts").addSnapshotListener { snapshot, _ ->
            val list = snapshot?.documents?.mapNotNull { it.toObject(Post::class.java) } ?: emptyList()
            // Sort by latest
            trySend(list.sortedByDescending { it.timestamp })
        }
        awaitClose { listener.remove() }
    }

    override fun getQuestions(): Flow<List<Question>> = callbackFlow {
        val listener = db.collection("questions").addSnapshotListener { snapshot, _ ->
            val list = snapshot?.documents?.mapNotNull { it.toObject(Question::class.java) } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    override fun getReviews(): Flow<List<Review>> = callbackFlow {
        val listener = db.collection("reviews").addSnapshotListener { snapshot, _ ->
            val list = snapshot?.documents?.mapNotNull { it.toObject(Review::class.java) } ?: emptyList()
            trySend(list)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getArtisanById(artisanId: String): Artisan? {
        if (artisanId.isBlank()) return null
        val doc = db.collection("artisans").document(artisanId).get().await()
        return doc.toObject(Artisan::class.java)?.copy(id = doc.id)
    }

    override suspend fun getArtFormById(artFormId: String): ArtForm? {
        if (artFormId.isBlank()) return null
        return db.collection("artForms").document(artFormId).get().await().toObject(ArtForm::class.java)
    }

    override fun getPostsByArtisan(artisanId: String): Flow<List<Post>> = callbackFlow {
        val listener = db.collection("posts").whereEqualTo("artisanId", artisanId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Post::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getEventsByArtisan(artisanId: String): Flow<List<Event>> = callbackFlow {
        val listener = db.collection("events").whereEqualTo("artisanId", artisanId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Event::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getReviewsByArtisan(artisanId: String): Flow<List<Review>> = callbackFlow {
        val listener = db.collection("reviews").whereEqualTo("artisanId", artisanId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Review::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getArtisansByArtForm(artFormId: String): Flow<List<Artisan>> = callbackFlow {
        val listener = db.collection("artisans").whereArrayContains("artFormIds", artFormId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Artisan::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addQuestion(question: Question) {
        val docRef = db.collection("questions").document()
        db.collection("questions").document(docRef.id).set(question.copy(id = docRef.id)).await()
    }

    override suspend fun addReview(review: Review) {
        val docRef = db.collection("reviews").document()
        db.collection("reviews").document(docRef.id).set(review.copy(id = docRef.id)).await()
    }

    override suspend fun checkInPassport(districtName: String) {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return
        // Use set+merge so it works even if the user document doesn't exist yet
        // (e.g. anonymous/guest users who were never written to Firestore)
        db.collection("users").document(uid)
            .set(mapOf("districtStamps" to FieldValue.arrayUnion(districtName)), SetOptions.merge())
            .await()
    }

    override suspend fun bookEvent(eventId: String, userId: String) {
        db.collection("bookings").add(
            mapOf(
                "eventId" to eventId,
                "userId" to userId,
                "timestamp" to System.currentTimeMillis()
            )
        ).await()
        
        // Also increment booking count on the event document
        db.collection("events").document(eventId)
            .update("bookingsCount", FieldValue.increment(1))
            .await()
    }

    override fun getMyBookedEventIds(userId: String): Flow<List<String>> = callbackFlow {
        val listener = db.collection("bookings")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.getString("eventId") } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun toggleLike(postId: String, userId: String) {
        // Use set+merge so it works even if the post document was not seeded yet
        db.collection("posts").document(postId)
            .set(mapOf("likesCount" to FieldValue.increment(1)), SetOptions.merge())
            .await()
    }

    override suspend fun addComment(comment: Comment) {
        val docRef = db.collection("comments").document()
        docRef.set(comment.copy(id = docRef.id)).await()
    }

    override fun getCommentsByPost(postId: String): Flow<List<Comment>> = callbackFlow {
        val listener = db.collection("comments").whereEqualTo("postId", postId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Comment::class.java) } ?: emptyList()
                trySend(list.sortedBy { it.timestamp })
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getPostById(postId: String): Post? {
        if (postId.isBlank()) return null
        return db.collection("posts").document(postId).get().await().toObject(Post::class.java)
    }
    
    // Utility to seed data once
    override suspend fun seedDatabase() {
        val artisansSnap = db.collection("artisans").limit(1).get().await()
        if (artisansSnap.isEmpty) {
            val mockData = com.example.karunada_kala.data.MockData
            mockData.mockArtisans.forEach { db.collection("artisans").document(it.id).set(it) }
            mockData.mockArtForms.forEach { db.collection("artForms").document(it.id).set(it) }
            mockData.mockEvents.forEach { db.collection("events").document(it.id).set(it) }
            mockData.mockPosts.forEach { db.collection("posts").document(it.id).set(it) }
            mockData.mockQuestions.forEach { db.collection("questions").document(it.id).set(it) }
            mockData.mockReviews.forEach { db.collection("reviews").document(it.id).set(it) }
        }
    }

    override suspend fun clearDatabase() {
        val collections = listOf("artisans", "artForms", "events", "posts", "questions", "reviews", "bookings", "comments")
        for (coll in collections) {
            val snapshot = db.collection(coll).get().await()
            for (doc in snapshot.documents) {
                db.collection(coll).document(doc.id).delete()
            }
        }
    }

    override suspend fun reseedDatabase() {
        clearDatabase()
        val mockData = com.example.karunada_kala.data.MockData
        mockData.mockArtisans.forEachIndexed { index, artisan ->
            val timestamp = System.currentTimeMillis() + index
            db.collection("artisans").document(artisan.id).set(artisan.copy(createdAt = timestamp))
        }
        mockData.mockArtForms.forEach { db.collection("artForms").document(it.id).set(it) }
        mockData.mockEvents.forEach { db.collection("events").document(it.id).set(it) }
        mockData.mockPosts.forEach { db.collection("posts").document(it.id).set(it) }
        mockData.mockQuestions.forEach { db.collection("questions").document(it.id).set(it) }
        mockData.mockReviews.forEach { db.collection("reviews").document(it.id).set(it) }
    }

    override suspend fun answerQuestion(questionId: String, answerText: String, guruId: String) {
        db.collection("questions").document(questionId)
            .set(
                mapOf("answerText" to answerText, "guruId" to guruId),
                SetOptions.merge()
            ).await()
    }

    override suspend fun addEvent(event: Event) {
        val docRef = if (event.id.isBlank()) db.collection("events").document()
                     else db.collection("events").document(event.id)
        docRef.set(event.copy(id = docRef.id)).await()
    }

    override suspend fun updateArtisanBio(artisanId: String, bio: String) {
        db.collection("artisans").document(artisanId)
            .set(mapOf("bio" to bio), SetOptions.merge())
            .await()
    }

    override suspend fun replyToReview(reviewId: String, replyText: String) {
        db.collection("reviews").document(reviewId)
            .set(mapOf("artisanReply" to replyText), SetOptions.merge())
            .await()
    }

    override fun getReviewsByEvent(eventId: String): Flow<List<Review>> = callbackFlow {
        val listener = db.collection("reviews").whereEqualTo("eventId", eventId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { it.toObject(Review::class.java) } ?: emptyList()
                trySend(list.sortedByDescending { it.timestamp })
            }
        awaitClose { listener.remove() }
    }

    override suspend fun uploadImage(localUriString: String, storagePath: String): String {
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child(storagePath)
        val uri = Uri.parse(localUriString)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
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
        db.collection("artisans").document(artisanId)
            .set(
                mapOf(
                    "name" to name,
                    "type" to type,
                    "imageUrl" to imageUrl,
                    "studioDescription" to description,
                    "studioImages" to imageUrls,
                    "lat" to lat,
                    "lng" to lng
                ),
                SetOptions.merge()
            ).await()
    }
}
