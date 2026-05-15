package com.example.karunada_kala.data.repository

import com.example.karunada_kala.domain.model.User
import com.example.karunada_kala.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // Fetch extra details from Firestore
                firestore.collection("users").document(user.uid).get()
                    .addOnSuccessListener { doc ->
                        val dbUser = doc.toObject(User::class.java)
                        if (dbUser != null) {
                            trySend(dbUser)
                        } else {
                            trySend(User(id = user.uid, email = user.email ?: "", role = "USER"))
                        }
                    }
                    .addOnFailureListener {
                        trySend(User(id = user.uid, email = user.email ?: "", role = "USER"))
                    }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user ?: throw Exception("Login failed")
            
            val doc = firestore.collection("users").document(user.uid).get().await()
            val dbUser = doc.toObject(User::class.java) ?: User(id = user.uid, email = email, role = "USER")
            
            Result.success(dbUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, pass: String, name: String, role: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user ?: throw Exception("Registration failed")
            
            val newUser = User(
                id = user.uid,
                name = name,
                email = email,
                role = role
            )
            
            firestore.collection("users").document(user.uid).set(newUser).await()
            
            // Also create artisan document if they are a STUDIO
            if (role == "STUDIO") {
                val artisanDoc = com.example.karunada_kala.domain.model.Artisan(
                    id = user.uid,
                    name = name,
                    type = "New Studio",
                    createdAt = System.currentTimeMillis()
                )
                firestore.collection("artisans").document(user.uid).set(artisanDoc).await()
            }
            
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginAsGuest(): Result<User> {
        return try {
            val result = auth.signInAnonymously().await()
            val user = result.user ?: throw Exception("Guest login failed")

            val guestUser = User(
                id = user.uid,
                name = "Guest User",
                role = "USER"
            )
            // Write a minimal Firestore doc so operations like passport check-in
            // don't crash with NOT_FOUND on the missing document.
            firestore.collection("users").document(user.uid)
                .set(guestUser, com.google.firebase.firestore.SetOptions.merge())
                .await()

            Result.success(guestUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
