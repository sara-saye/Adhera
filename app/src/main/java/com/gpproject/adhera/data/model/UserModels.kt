package com.gpproject.adhera.data.model

// data/model/UserModels.kt
sealed class UserRole {
    object Doctor : UserRole()
    object Adult : UserRole()
    object Parent : UserRole()
}

// data/model/UserModels.kt

data class UserProfile(
    val uid: String = "",
    val role: String = "",
    val email: String = "",
    val name: String? = null,        // للدكتور
    val nickname: String? = null,    // للطفل/البالغ
    val gender: String? = null,
    val age: String? = null,
    val childWillUseThisPhone: Boolean = true,
    val linkParentPhone: Boolean = false,

    // إضافات المشروع الخاصة بكِ
    val patientIds: List<String> = emptyList(), // للدكتور فقط
    val detectionResults: Map<String, Any> = emptyMap() // نتائج الـ 5 موديلات
)