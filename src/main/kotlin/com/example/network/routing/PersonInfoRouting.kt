package com.example.network.routing

import com.example.database.model.users.Users
import com.example.model.PersonInfo
import com.example.network.model.request.PersonInfoRequest
import com.example.network.model.response.PersonInfoResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configurePersonInfoRouting() {
    routing {
        authenticate("jwt") {
            put("/personsinfo"){
//                val principal = call.principal<JWTPrincipal>()
//                val userId = principal?.getClaim("userId", Long::class)
//
//                if (userId == null){
//                    call.respond(
//                        HttpStatusCode.BadRequest,
//                        "Authentication failed: Failed to parse Access token"
//                    )
//                    return@put
//                }
//
//                val persInfo = Users.getUserById(userId)
//                if(persInfo != null){
//                    call.respond(HttpStatusCode.Conflict)
//                    return@put
//                }
//
//                val request = call.receive(PersonInfoRequest::class)
//
//                val personInfo = PersonsInfo.insertPersonInfo(
//                    PersonInfo(
//                        userId = userId,
//                        name = request.name,
//                        surname = request.surname,
//                        age = request.age,
//                        categoryId = request.categoryId,
//                        doctorStatus = false
//                    )
//                )
//
//                if(personInfo == null) {
//                    call.respond(HttpStatusCode.BadRequest)
//                    return@put
//                }
//
//                call.respond(HttpStatusCode.Created)
            }

            get("/personsinfo") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@get
                }

                val personInfo = Users.getUserById(userId)
                if(personInfo == null){
                    call.respond(HttpStatusCode.Conflict)
                    return@get
                }

                call.respond(HttpStatusCode.OK, PersonInfoResponse(
                    id = personInfo.id,
                    name = personInfo.name,
                    surname = personInfo.surname,
                    age = personInfo.age,
                    categoryId = personInfo.categoryId,
                    doctorStatus = personInfo.doctorStatus,
                    icon = personInfo.icon
                ))
            }


            get("/personsinfo/{personId}") {

                val paramUserId = call.parameters["personId"]
                if (paramUserId == null) {
                    call.respond(HttpStatusCode.Conflict, "Param friendId is absent")
                    return@get
                }

                val userId: Long
                try {
                    userId = paramUserId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.Conflict, "Invalid param friendId")
                    return@get
                }

                val personInfo = Users.getUserById(userId)
                if(personInfo == null){
                    call.respond(HttpStatusCode.Conflict)
                    return@get
                }

                call.respond(
                        HttpStatusCode.OK, PersonInfoResponse(
                        id = personInfo.id,
                        name = personInfo.name,
                        surname = personInfo.surname,
                        age = personInfo.age,
                        categoryId = personInfo.categoryId,
                        doctorStatus = personInfo.doctorStatus,
                        icon = personInfo.icon
                    )
                )
            }
        }
    }
}