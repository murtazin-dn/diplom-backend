package com.example.network.routing

import com.example.controller.PostController
import com.example.database.model.Categories
import com.example.database.model.Posts
import com.example.model.Post
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
                    val response = controller.getLike(call)
                    call.respond(response.code, response.body)
                }
                post {
                    val response = controller.setLike(call)
                    call.respond(response.code, response.body)
                }
                delete {
                    val response = controller.unsetLike(call)
                    call.respond(response.code, response.body)
                }
            }
            route("/{postId}/comments"){
                get {
                    val response = controller.getComments(call)
                    call.respond(response.code, response.body)
                }
                post {
                    val response = controller.createComment(call)
                    call.respond(response.code, response.body)
                }
            }

            post{
                val request = call.receive<PostRequest>()
                val response = controller.createPost(request, call)
                call.respond(response.code, response.body)
            }

            get("/{postId}") {
                val response = controller.getPostById(call)
                call.respond(response.code, response.body)
            }

            get{
                val response = controller.getPosts(call)
                call.respond(response.code, response.body)
            }
        }
}