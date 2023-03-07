package com.example.network.routing

import com.example.database.model.users.Categories
import com.example.database.model.users.Posts
import com.example.model.Post
import com.example.network.model.request.CreatePostRequest
import com.example.network.model.request.LoginRequest
import com.example.network.model.response.PostResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.date.*
import java.time.Instant
import java.util.Date


fun Application.configurePostRouting() {

    routing {
        authenticate("jwt") {
            post("/posts") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@post
                }

                val request = call.receive(CreatePostRequest::class)

                if(Categories.getCategoryById(request.categoryId) == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val post = Posts.insertPost(
                    Post(
                        id = 0,
                        userId = userId,
                        title = request.title,
                        text = request.text,
                        categoryId = request.categoryId,
                        timeAtCreation = Instant.now(),
                        likesCount = 0,
                        commentsCount = 0
                    )
                )

                if(post == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                call.respond(
                    HttpStatusCode.Created,
                    PostResponse(
                        id = post.id,
                        userId = post.userId,
                        title = post.title,
                        text = post.text,
                        categoryId = post.categoryId,
                        timeAtCreation = post.timeAtCreation,
                        likesCount = post.likesCount,
                        commentsCount = post.commentsCount
                    )
                )
            }

            get("/posts/{postId}") {

                val paramPostId = call.parameters["postId"]
                if (paramPostId == null) {
                    call.respond(HttpStatusCode.Conflict, "Param postId is absent")
                    return@get
                }

                val postId: Long
                try {
                    postId = paramPostId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.Conflict, "Invalid param postId")
                    return@get
                }

                val post = Posts.getPostById(postId)
                if(post == null){
                    call.respond(HttpStatusCode.Conflict)
                    return@get
                }
                call.respond(
                    HttpStatusCode.OK,
                    PostResponse(
                        id = post.id,
                        userId = post.userId,
                        title = post.title,
                        text = post.text,
                        categoryId = post.categoryId,
                        timeAtCreation = post.timeAtCreation,
                        likesCount = post.likesCount,
                        commentsCount = post.commentsCount
                    )
                )

            }
        }
    }



}