package com.gpproject.adhera.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.gpproject.adhera.data.model.UserProfile

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ================= SIGN UP =================
    fun signUp(
        email: String,
        password: String,
        user: UserProfile,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                // 🔥 Email Verification
                result.user?.sendEmailVerification()

                val uid = result.user?.uid ?: ""

                db.collection("users").document(uid)
                    .set(user.copy(uid = uid))
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.message) }

            }
            .addOnFailureListener { onError(it.message) }
    }

    // ================= LOGIN =================
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                // 🔥 تأكد إن الإيميل verified
                if (result.user?.isEmailVerified == true) {
                    onSuccess()
                } else {
                    onError("Please verify your email first")
                }

            }
            .addOnFailureListener { onError(it.message) }
    }

    // ================= FORGOT PASSWORD =================
    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message) }
    }

    // ================= GOOGLE SIGN IN =================
    fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message) }
    }

    fun logout() = auth.signOut()

    fun isUserLoggedIn() = auth.currentUser != null
}