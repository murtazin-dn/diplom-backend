package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonInfo (
    val userId: Long,
    val name: String,
    val surname: String,
    val age: Int,
    val categoryId: Long,
    val doctorStatus: Boolean,
    val icon: String?
    )