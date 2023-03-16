package com.example.network.routing


import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing{
        route("/api/v1"){
            configureAuthRouting()
            authenticate("jwt") {
                configureSubscribersRouting()
                configurePostRouting()
                configureUserInfoRouting()
                configureChatRouting()
                configureMessageRouting()
            }

        }
    }

}
