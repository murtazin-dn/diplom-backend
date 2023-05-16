package com.example.controller

import com.example.database.model.FCMTokens
import com.example.model.FCMToken
import com.example.network.model.HttpResponse
import com.example.network.model.request.FCMTokenRequest
import com.example.utils.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*

class NotificationsControllerImpl: NotificationsController {
}

interface NotificationsController {
    suspend fun subscribeNotification(call: ApplicationCall): HttpResponse<String>{
        return try{
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.getClaim("userId", Long::class)!!
            val token = call.receive<FCMTokenRequest>()
            println(" token: ${token.token}")
            FCMTokens.insertToken(
                FCMToken(userId, token.token)
            )?.let {
                HttpResponse.ok("")
            } ?: HttpResponse.badRequest("")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }
}
