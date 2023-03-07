package com.example.network.routing

import com.example.database.model.users.PostsLikes
import com.example.model.PostLike
import com.example.network.model.response.IsLikeResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configurePostLikesRouting() {
    routing {
        authenticate("jwt") {
            post("/posts/{postId}/like"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@post
                }

                val paramPostId = call.parameters["postId"]
                if (paramPostId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Param postId is absent")
                    return@post
                }
                val postId: Long
                try {
                    postId = paramPostId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param postId")
                    return@post
                }

                val postLike = PostsLikes.selectPostLike(
                    PostLike(
                        postId = postId,
                        userId = userId
                    )
                )

                if(postLike != null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    PostsLikes.insertPostLike(
                        PostLike(
                            postId = postId,
                            userId = userId
                        )
                    )
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                call.respond(HttpStatusCode.Created)
            }

            get("/posts/{postId}/like") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@get
                }

                val paramPostId = call.parameters["postId"]
                if (paramPostId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Param postId is absent")
                    return@get
                }
                val postId: Long
                try {
                    postId = paramPostId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param postId")
                    return@get
                }

                val countPostLike = PostsLikes.selectPostLike(
                    PostLike(
                        postId = postId,
                        userId = userId
                    )
                )
                val result = countPostLike != null


                call.respond(HttpStatusCode.Created, IsLikeResponse(result))
            }

            delete("/posts/{postId}/like"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Long::class)

                if (userId == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Authentication failed: Failed to parse Access token"
                    )
                    return@delete
                }

                val paramPostId = call.parameters["postId"]
                if (paramPostId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Param postId is absent")
                    return@delete
                }
                val postId: Long
                try {
                    postId = paramPostId.toLong()
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, "Invalid param postId")
                    return@delete
                }

                try {
                    PostsLikes.deletePostLike(
                        PostLike(
                            postId = postId,
                            userId = userId
                        )
                    )
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                call.respond(HttpStatusCode.Created)
            }
        }
    }
}