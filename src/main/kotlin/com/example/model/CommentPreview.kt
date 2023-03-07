package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentPreview(
    val id: Long,
    val userId: Long,
    val postId: Long,
    val date: Long,
    val text: String,
    val userName: String,
    val userSurName: String,
    val icon: String?
)
