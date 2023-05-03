package com.example.network.routing

import com.example.controller.PostController
import com.example.database.model.Categories
import com.example.database.model.Posts
import com.example.model.Post
import com.example.network.model.HttpResponse
import com.example.network.model.request.PostRequest
import com.example.network.model.response.PostResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.Instant


fun Route.configurePostRouting() {

    val controller by inject<PostController>()

        route("/posts"){

            route("/{postId}/likes"){
                get {
                    controller.getLike(call).let {response ->
                        when(response){
                            is HttpResponse.Error -> call.respond(response.code, response.message)
                            is HttpResponse.Success -> call.respond(response.code, response.body)
                        }
                    }
                }
                post {
                    controller.setLike(call).let {response ->
                        when(response){
                            is HttpResponse.Error -> call.respond(response.code, response.message)
                            is HttpResponse.Success -> call.respond(response.code, response.body)
                        }
                    }
                }
                delete {
                    controller.unsetLike(call).let {response ->
                        when(response){
                            is HttpResponse.Error -> call.respond(response.code, response.message)
                            is HttpResponse.Success -> call.respond(response.code, response.body)
                        }
                    }
                }
            }
            route("/{postId}/comments"){
                get {
                    controller.getComments(call).let {response ->
                        when(response){
                            is HttpResponse.Error -> call.respond(response.code, response.message)
                            is HttpResponse.Success -> call.respond(response.code, response.body)
                        }
                    }
                }
                post {
                    controller.createComment(call).let {response ->
                        when(response){
                            is HttpResponse.Error -> call.respond(response.code, response.message)
                            is HttpResponse.Success -> call.respond(response.code, response.body)
                        }
                    }
                }
            }

            post{
                val request = call.receive<PostRequest>()
                controller.createPost(request, call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }

            get("/{postId}") {
                controller.getPostById(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }

            get{
                controller.getPosts(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
        }
}