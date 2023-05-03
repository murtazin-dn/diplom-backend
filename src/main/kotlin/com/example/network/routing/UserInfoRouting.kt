package com.example.network.routing

import com.example.controller.UserInfoController
import com.example.database.model.Users
import com.example.network.model.HttpResponse
import com.example.network.model.response.UserInfoResponse
import com.example.utils.BadRequestException
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.configureUserInfoRouting() {

    val controller by inject<UserInfoController>()

        route("/users/info"){
            get{
                controller.getMyUserInfo(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
        }
        route("/users"){
            get{
                controller.findUsers(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
        }
        route("/users/info/{userId}"){
            get{
                controller.getUserInfo(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
        }
        route("/users/photo"){
            post {
                val multipart = call.receiveMultipart()
                var fileName: String? = null
                try{
                    multipart.forEachPart { partData ->
                        when(partData){
                            is PartData.FormItem -> throw BadRequestException("error upload image")
                            is PartData.FileItem ->{
                                fileName = partData.save("./users_photo/")
                            }
                            is PartData.BinaryItem -> throw BadRequestException("error upload image")
                            is PartData.BinaryChannelItem -> throw BadRequestException("error upload image")
                        }
                    }
                    val userId = call.principal<JWTPrincipal>()!!.getClaim("userId", Long::class)!!
                    Users.updatePhoto(userId, fileName!!)
                    call.respond(HttpStatusCode.OK, mapOf("name" to fileName.toString()))
                } catch (ex: Exception) {
                    File("./users_photo/$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError,"Error")
                }
            }
        }
}

fun PartData.FileItem.save(path: String): String {
    val fileBytes = streamProvider().readBytes()
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    val folder = File(path)
    folder.mkdir()
    println("Path = $path $fileName")
    File("$path$fileName").writeBytes(fileBytes)
    return fileName
}