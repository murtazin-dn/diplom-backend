package com.example.model

import com.example.network.model.response.CategoryResponse
import kotlinx.serialization.Serializable

@Serializable
data class Category (
    val id: Long,
    val name: String
){
    fun toCategoryResponse() = CategoryResponse(
        id = this.id,
        name = this.name
    )
}