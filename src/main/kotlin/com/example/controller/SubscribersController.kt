package com.example.controller

import com.example.database.model.Subscribers
import com.example.database.model.Users
import com.example.model.Subscriber
import com.example.model.UserInfo
import com.example.network.model.HttpResponse
import com.example.network.model.response.SubscribersResponse
import com.example.utils.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class SubscribersControllerImpl: SubscribersController{
    override suspend fun getSubscribers(call: ApplicationCall): HttpResponse<List<UserInfo>> {
        return try{
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val subscribers = Subscribers.getSubscribersByUserId(userId)
            HttpResponse.ok(subscribers)
        }catch(e: Exception){
            HttpResponse.badRequest(e.message.toString())
        }
    }

    override suspend fun subscribe(call: ApplicationCall): HttpResponse<String> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val subscribeId = call.parameters["userId"]?.toLongOrNull()
                ?: throw BadRequestException("Invalid param userId")
            if(Users.getUserById(subscribeId) != null){
                if(Subscribers.getSubscribe(userId, subscribeId) != null)
                    throw BadRequestException("Subscription already exists")
                Subscribers.createSubscribe(Subscriber(userId, subscribeId))?.let {
                    HttpResponse.ok("")
                } ?: throw BadRequestException("failed to subscribe")
            } else throw BadRequestException("user with this id does not exist")
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun unsubscribe(call: ApplicationCall): HttpResponse<String> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val subscribeId = call.parameters["userId"]?.toLongOrNull()
                ?: throw BadRequestException("Invalid param userId")
            if(Users.getUserById(subscribeId) != null){
                if(Subscribers.getSubscribe(userId, subscribeId) == null)
                    throw BadRequestException("subscription does not exist")
                if(Subscribers.deleteSubscribe(userId, subscribeId) > 0){
                    HttpResponse.ok("")
                } else throw BadRequestException("Failed to subscribe")
            } else throw BadRequestException("User with this id does not exist")
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }


}
interface SubscribersController {
    suspend fun getSubscribers(call: ApplicationCall): HttpResponse<List<UserInfo>>
    suspend fun subscribe(call: ApplicationCall): HttpResponse<String>
    suspend fun unsubscribe(call: ApplicationCall): HttpResponse<String>
}