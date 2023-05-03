package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentPreview(
    val id: Long,
    val user: UserInfo,
    val postId: Long,
    val date: Long,
    val text: String
)
