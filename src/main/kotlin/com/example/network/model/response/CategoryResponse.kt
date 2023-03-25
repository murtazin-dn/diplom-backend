package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Long,
    val name: String
)
