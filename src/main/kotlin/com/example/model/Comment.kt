package com.example.model

import com.example.network.model.response.CommentResponse

data class Comment(
    val id: Long,
    val userId: Long,
    val postId: Long,
    val date: Long,
    val text: String
){
    fun toCommentResponse() = CommentResponse(
        id = this.id,
        userId = this.userId,
        postId = this.postId,
        date = this.date,
        text = this.text
    )
}
