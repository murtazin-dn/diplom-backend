package com.example.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val surname: String,
    val dateOfBirthday: Long,
    val categoryId: Long
)
