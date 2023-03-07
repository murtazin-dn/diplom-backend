package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateFriendResponse(
    val userId: Long,
    val friendId: Long,
    val status: Int
)
