package com.example.network.model.request

import com.example.model.Category
import kotlinx.serialization.Serializable

@Serializable
data class PostRequest (
    val title: String,
    val text: String,
    val categoryId: Long
    )