package com.example.model

import io.ktor.server.websocket.*
import io.ktor.websocket.*

data class Member(
    val userId: Long,
    val session: WebSocketServerSession
)
