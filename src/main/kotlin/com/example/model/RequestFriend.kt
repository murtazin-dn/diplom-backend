package com.example.model

data class RequestFriend(
    val id: Long,
    val userId: Long,
    val friendId: Long,
    val status: Int
)
