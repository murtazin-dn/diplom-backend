package com.example.model

import java.time.Instant

data class Post(
    val id: Long,
    val userId: Long,
    val title: String,
    val text: String,
    val categoryId: Long,
    val timeAtCreation: Instant,
    val likesCount: Long,
    val commentsCount: Long
)
