package com.gpproject.adhera.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    suspend fun getCurrentUserProfile(): Result<UserProfile> {

        return try {

            val uid = getCurrentUserId()

            if (uid.isBlank()) {
                Result.failure(IllegalStateException("No authenticated user"))
            } else {
                val profile = firestore.collection("users")
                    .document(uid)
                    .get()
                    .await()
                    .toObject(UserProfile::class.java)
                    ?: UserProfile(uid = uid, email = auth.currentUser?.email.orEmpty())

                Result.success(profile)
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun updateAccountProfile(
        nickname: String,
        email: String,
        password: String?
    ): Result<Unit> {

        return try {

            val user = auth.currentUser
                ?: return Result.failure(IllegalStateException("No authenticated user"))

            val uid = user.uid
            val trimmedNickname = nickname.trim()
            val trimmedEmail = email.trim()

            if (trimmedEmail.isNotBlank() && trimmedEmail != user.email) {
                user.updateEmail(trimmedEmail).await()
            }

            if (!password.isNullOrBlank()) {
                user.updatePassword(password).await()
            }

            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(trimmedNickname)
                    .build()
            ).await()

            val updates = mutableMapOf<String, Any>(
                "nickname" to trimmedNickname,
                "email" to trimmedEmail
            )

            firestore.collection("users")
                .document(uid)
                .set(updates, SetOptions.merge())
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun deleteCurrentAccount(): Result<Unit> {

        return try {

            val user = auth.currentUser
                ?: return Result.failure(IllegalStateException("No authenticated user"))

            val uid = user.uid

            firestore.collection("users")
                .document(uid)
                .delete()
                .await()

            user.delete().await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    fun logout() {

        auth.signOut()
    }

    suspend fun reloadUser() {

        auth.currentUser?.reload()?.await()
    }
}
