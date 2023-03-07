package com.example.network.model.request

data class MessageRequest(
    val id: Long,
    val chatId: Long,
    val userId: Long,
    val text: String,
    val date: Long
)
