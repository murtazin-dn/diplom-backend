package com.example.network.routing

import com.example.database.model.users.Users
import com.example.model.User
import com.example.network.model.request.LoginRequest
import com.example.network.model.request.RegisterRequest
import com.example.network.model.response.RegisterResponse
import com.example.secure.JwtTokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthRouting() {
    routing {
        post("/signup") {
            val request = call.receive(RegisterRequest::class)

            val areFieldsBlank = request.login.isBlank() || request.password.isBlank()


            if(areFieldsBlank){
                call.respond(HttpStatusCode.Conflict)
                return@post
            }

            if(request.email != null){
                if(Users.getUserByEmail(request.email) != null){
                    call.respond(HttpStatusCode.Conflict, "Email is already exists")
                    return@post
                }
            }

            if(Users.getUserByLogin(request.login) != null){
                call.respond(HttpStatusCode.Conflict, "Login is already exists")
                return@post
            }


            val user = Users.insert(
                User(
                    id = 0,
                    login = request.login,
                    password = request.password,
                    email = request.email,
                    name = request.name,
                    surname = request.surname,
                    age = request.age,
                    categoryId = request.categoryId,
                    doctorStatus = false,
                    icon = request.icon
                )
            )
            if(user == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val token = JwtTokenService.generate(user.id)
            call.respond(RegisterResponse(token))

        }
        post("/signin") {
            val request = call.receive(LoginRequest::class)

            val areFieldsBlank = request.login.isBlank() || request.password.isBlank()

            if(areFieldsBlank){
                call.respond(HttpStatusCode.Conflict)
                return@post
            }

            val user = Users.getUserByLogin(request.login)

            if(user == null){
                call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
                return@post
            }

            if(request.password != user.password) {
                call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
                return@post
            }

            val token = JwtTokenService.generate(user.id)
            call.respond(RegisterResponse(token))

        }


        authenticate("jwt") {
            get("/secret") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)
                call.respond(HttpStatusCode.OK, userId.toString())
            }
        }
    }
}