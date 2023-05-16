package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class PostInfo(
    val id: Long,
    val user: UserInfo,
    val title: String,
    val text: String,
    val category: Category,
    val timeAtCreation: Long,
    val likesCount: Long,
    val commentsCount: Long,
    var isLikeEnabled: Boolean,
    val images: MutableList<String>
)
