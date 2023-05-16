package com.example.model

import com.example.network.model.response.PostResponse
import java.time.Instant

data class Post(
    val id: Long,
    val userId: Long,
    val title: String,
    val text: String,
    val categoryId: Long,
    val timeAtCreation: Long,
    val likesCount: Long,
    val commentsCount: Long
) {
    fun toPostResponse(images: List<String>) = PostResponse(
        id = this.id,
        userId = this.userId,
        title = this.title,
        text = this.text,
        categoryId = this.categoryId,
        timeAtCreation = this.timeAtCreation,
        commentsCount = this.commentsCount,
        likesCount = this.likesCount,
        images = images
    )
}
