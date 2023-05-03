package com.example.controller

import com.example.database.model.Subscribers
import com.example.database.model.Users
import com.example.network.model.HttpResponse
import com.example.network.model.response.ProfileResponse
import com.example.network.model.response.UserInfoResponse
import com.example.utils.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class UserInfoControllerImpl: UserInfoController{
    override suspend fun findUsers(call: ApplicationCall): HttpResponse<List<ProfileResponse>> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val text = call.parameters["text"] ?: throw BadRequestException("Invalid param text")
            val list = Users.findUsers(text).map { userInfo ->
                val isSubscribe = Subscribers.getSubscribe(userId, userInfo.id) != null
                ProfileResponse(userInfo.toUserInfoResponse(), isSubscribe)
            }
            HttpResponse.ok(list)
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getMyUserInfo(call: ApplicationCall): HttpResponse<UserInfoResponse> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            Users.getUserInfo(userId)?.let { userInfo ->
                HttpResponse.ok(userInfo.toUserInfoResponse())
            } ?: throw BadRequestException("User with this id does not exists")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getUserInfo(call: ApplicationCall): HttpResponse<ProfileResponse> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val subscriberId = call.parameters["userId"]?.toLong() ?: throw BadRequestException("Invalid param userId")
            Users.getUserInfo(subscriberId)?.let { userInfo ->
                val isSubscribe = Subscribers.getSubscribe(userId, subscriberId) != null
                HttpResponse.ok(ProfileResponse(userInfo.toUserInfoResponse(), isSubscribe))
            } ?: throw BadRequestException("User with this id does not exists")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

}
interface UserInfoController {

    suspend fun findUsers(call: ApplicationCall): HttpResponse<List<ProfileResponse>>
    suspend fun getMyUserInfo(call: ApplicationCall): HttpResponse<UserInfoResponse>
    suspend fun getUserInfo(call: ApplicationCall): HttpResponse<ProfileResponse>
}