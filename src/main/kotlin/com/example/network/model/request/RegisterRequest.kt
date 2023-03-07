package com.example.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String?,
    val login: String,
    val password: String,
    val name: String,
    val surname: String,
    val age: Int,
    val categoryId: Long,
    val icon: String?
)
