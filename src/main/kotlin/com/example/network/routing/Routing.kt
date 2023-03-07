package com.example.network.routing


import com.example.network.messages.controller.MessagesController
import io.ktor.server.application.*

fun Application.configureRouting() {
    configureAuthRouting()
    configurePostRouting()
    configurePersonInfoRouting()
    configureFriendRouting()
    configurePostLikesRouting()
    configureWebSocketRouting(MessagesController())
    configureChatRouting()
    configureMessageRouting()
}
