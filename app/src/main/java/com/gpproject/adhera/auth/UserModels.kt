package com.gpproject.adhera.auth

sealed class UserRole(val value: String) {

    data object Doctor : UserRole("Doctor")

    data object AdultChild : UserRole("AdultChild")

    data object Parent : UserRole("Parent")
}

enum class Gender {
    MALE,
    FEMALE
}

data class UserProfile(

    val uid: String = "",

    val role: String = "",

    val email: String = "",

    // Doctor
    val name: String? = null,

    // Adult / Child
    val nickname: String? = null,

    val gender: Gender? = null,

    val age: Int? = null,

    // Parent
    val childWillUseThisPhone: Boolean = true,

    val linkParentPhone: Boolean = false,

    // Doctor
    val patientIds: List<String> = emptyList(),

    // Detection
    val detectionResults: Map<String, Any> = emptyMap()
)