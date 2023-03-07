package com.example.network.model.response

import com.example.model.CommentPreview
import kotlinx.serialization.Serializable

@Serializable
data class CommentsListResponse(
    val list: List<CommentPreview>
)
