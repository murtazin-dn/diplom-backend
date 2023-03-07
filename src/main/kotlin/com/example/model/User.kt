package com.example.model

data class User(
    val id: Long,
    val login: String,
    val password: String,
    val email: String?,
    val name: String,
    val surname: String,
    val age: Int,
    val categoryId: Long,
    val doctorStatus:Boolean,
    val icon: String?
)
