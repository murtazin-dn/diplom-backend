package com.example.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PersonInfoResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val age: Int,
    val categoryId: Long,
    val doctorStatus: Boolean,
    val icon: String?
)

