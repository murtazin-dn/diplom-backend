package com.example.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class PersonInfoRequest(
    val name: String,
    val surname: String,
    val age: Int,
    val categoryId: Long
)
