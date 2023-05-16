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

fun Route.configurePhotoRouting() {
    route("/image") {
        get("/{name}") {
            // get filename from request url
            val filename = call.parameters["name"]!!
            // construct reference to file
            // ideally this would use a different filename
            val file = File("./users_photo/$filename")
            if (file.exists()) {
                call.respondFile(file)
            } else call.respond(HttpStatusCode.NotFound)
        }

        post {
            val multipart = call.receiveMultipart()
            var fileName: String? = null
            try {
                multipart.forEachPart { partData ->
                    when (partData) {
                        is PartData.FormItem -> throw BadRequestException("error upload image")
                        is PartData.FileItem -> {
                            fileName = partData.save("./users_photo/")
                        }

                        is PartData.BinaryItem -> throw BadRequestException("error upload image")
                        is PartData.BinaryChannelItem -> throw BadRequestException("error upload image")
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("name" to fileName.toString()))
            } catch (ex: Exception) {
                println("message error: ${ex.message}")
                println(ex.stackTraceToString())
                File("./users_photo/$fileName").delete()
                call.respond(HttpStatusCode.InternalServerError, "Error")
            }
        }


    }

}