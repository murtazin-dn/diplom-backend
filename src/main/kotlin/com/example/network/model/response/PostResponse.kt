package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val text: String,
    val categoryId: Long,
    val timeAtCreation: Long,
    val likesCount: Long,
    val commentsCount: Long,
    val images: List<String>
)

