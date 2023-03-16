package com.example.model

data class User(
    val id: Long,
    val password: String,
    val email: String,
    val name: String,
    val surname: String,
    val dateOfBirthday: Long,
    val categoryId: Long,
    val doctorStatus:Boolean,
    val icon: String?
)
