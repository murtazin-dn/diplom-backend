package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: Long,
    val userId: Long,
    val postId: Long,
    val date: Long,
    val text: String
)
