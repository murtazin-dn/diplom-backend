package com.example.network.routing

import com.example.database.model.users.PostsComments
import com.example.model.Comment
import com.example.network.model.request.PostCommentRequest
import com.example.network.model.response.CommentsListResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configurePostCommentsRouting() {
    routing {
        authenticate("jwt") {
            post("posts/{postId}/comment") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("UserId", Long::class)
                val postId = call.parameters["postId"]?.toLong()

                val request = call.receive(PostCommentRequest::class)

                val comment = PostsComments.createComment(
                    Comment(
                        id = 0,
                        userId = userId!!,
                        postId = postId!!,
                        text = request.text,
                        date = System.currentTimeMillis()
                    )
                )

                if(comment == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                call.respond(HttpStatusCode.OK)

            }

            get("posts/{postId}/comment") {
//                val postId = call.parameters["postId"]?.toLong()
//                val comments = PostsComments.getCommentsByPostId(postId)
//
//                call.respond(HttpStatusCode.OK, CommentsListResponse(comments))

            }
        }


    }
}