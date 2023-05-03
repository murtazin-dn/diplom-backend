package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val category: CategoryResponse,
    val doctorStatus: Boolean,
    val dateOfBirthday: Long,
    val icon: String?
)

