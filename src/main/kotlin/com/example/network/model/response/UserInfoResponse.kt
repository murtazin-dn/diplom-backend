package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val category: String,
    val doctorStatus: Boolean,
    val icon: String?
)

