package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoriesResponse(
    val list: List<CategoryResponse>
)
