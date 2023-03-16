package com.example.controller

import com.example.database.model.Users
import com.example.model.UserInfo
import com.example.network.model.HttpResponse
import com.example.network.model.response.UserInfoResponse
import com.example.utils.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

class UserInfoControllerImpl: UserInfoController{
    override suspend fun getMyUserInfo(call: ApplicationCall): HttpResponse<Any> {
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

    override suspend fun getUserInfo(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val userId = call.parameters["userId"]?.toLong() ?: throw BadRequestException("Invalid param userId")
            Users.getUserInfo(userId)?.let { userInfo ->
                HttpResponse.ok(userInfo.toUserInfoResponse())
            } ?: throw BadRequestException("User with this id does not exists")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

}
interface UserInfoController {
    suspend fun getMyUserInfo(call: ApplicationCall): HttpResponse<Any>
    suspend fun getUserInfo(call: ApplicationCall): HttpResponse<Any>
}