package com.example.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PostCommentRequest(
    val text: String
)
