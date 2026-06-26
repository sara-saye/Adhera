package com.gpproject.adhera.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository(

    private val auth: FirebaseAuth,

    private val firestore: FirebaseFirestore
) {

    // =========================
    // Email Signup
    // =========================

    suspend fun signUp(

        email: String,

        password: String
    ): Result<Unit> {

        return try {

            auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            auth.currentUser?.sendEmailVerification()?.await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // =========================
    // Login
    // =========================

    suspend fun login(

        email: String,

        password: String
    ): Result<Unit> {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // =========================
    // Reset Password
    // =========================

    suspend fun resetPassword(

        email: String
    ): Result<Unit> {

        return try {

            auth.sendPasswordResetEmail(email)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    // =========================
    // Save User Data
    // =========================

    suspend fun saveAdditionalInfo(

        profile: UserProfile
    ): Result<Unit> {

        return try {

            firestore.collection("users")
                .document(profile.uid)
                .set(profile)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    // =========================
    // Check Email Verification
    // =========================

    fun isEmailVerified(): Boolean {

        auth.currentUser?.reload()

        return auth.currentUser?.isEmailVerified == true
    }

    // =========================
    // Current User
    // =========================

    fun getCurrentUserId(): String {

        return auth.currentUser?.uid ?: ""
    }

    suspend fun getCurrentUserRole(): String? {

        val uid = getCurrentUserId()

        if (uid.isBlank()) return null

        return firestore.collection("users")
            .document(uid)
            .get()
            .await()
            .getString("role")
    }

    fun logout() {

        auth.signOut()
    }

    suspend fun reloadUser() {

        auth.currentUser?.reload()?.await()
    }
}
