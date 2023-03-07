package com.example.model

data class Comment(
    val id: Long,
    val userId: Long,
    val postId: Long,
    val date: Long,
    val text: String
)
