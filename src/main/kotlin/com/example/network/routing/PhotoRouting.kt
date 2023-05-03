package com.example.network.routing

import com.example.database.model.Users
import com.example.utils.BadRequestException
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.configurePhotoRouting(){
    route("/users/photo"){
        get("/{name}") {
            // get filename from request url
            val filename = call.parameters["name"]!!
            // construct reference to file
            // ideally this would use a different filename
            val file = File("./users_photo/$filename")
            if(file.exists()) {
                call.respondFile(file)
            }
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}