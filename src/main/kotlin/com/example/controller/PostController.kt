package com.example.controller

import com.example.database.model.Categories
import com.example.database.model.Posts
import com.example.database.model.PostsComments
import com.example.database.model.PostsLikes
import com.example.model.Comment
import com.example.model.Post
import com.example.model.PostLike
import com.example.network.model.HttpResponse
import com.example.network.model.request.PostCommentRequest
import com.example.network.model.request.PostRequest
import com.example.network.model.response.CommentsListResponse
import com.example.network.model.response.IsLikeResponse
import com.example.network.model.response.PostResponse
import com.example.utils.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import java.time.Instant

class PostControllerImpl: PostController {
    override suspend fun createPost(postRequest: PostRequest, call: ApplicationCall): HttpResponse<Any>{
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            validatePostFields(postRequest)
            if (Categories.getCategoryById(postRequest.categoryId) == null)
                throw BadRequestException("Category is absent")
            Posts.insertPost(
                Post(
                    id = 0,
                    userId = userId,
                    title = postRequest.title,
                    text = postRequest.text,
                    categoryId = postRequest.categoryId,
                    timeAtCreation = Instant.now().toEpochMilli(),
                    likesCount = 0,
                    commentsCount = 0
                )
            )?.let { HttpResponse.ok(it.toPostResponse())
            } ?: throw BadRequestException("Error insert post")
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getPostById(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val postId = call.parameters["postId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param post id")
            Posts.getPostById(postId)?.let { post ->
                HttpResponse.ok(post.toPostResponse())
            } ?: throw BadRequestException("Post with this id is absent")
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getPosts(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.getClaim("userId", Long::class)!!
            call.request.queryParameters["userId"]?.toLongOrNull()?.let {
                val posts = Posts.getPostsByUserId(it).map { post ->
                    PostsLikes.selectPostLike(PostLike(post.id, userId))?.let {
                        post.isLikeEnabled = true
                    }
                    post
                }
                return HttpResponse.ok(posts)
            }
            val posts = Posts.getPostsSubscribers(userId).map { post ->
                PostsLikes.selectPostLike(PostLike(post.id, userId))?.let {
                    post.isLikeEnabled = true
                }
                post
            }
            HttpResponse.ok(posts)
        } catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun setLike(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val postId = call.parameters["postId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param post id")
            Posts.getPostById(postId)?.let {
                if(PostsLikes.selectPostLike(PostLike(postId, userId)) != null)
                    throw BadRequestException("Like already set")
                PostsLikes.insertPostLike(
                    PostLike(
                        userId = userId,
                        postId = postId
                    )
                )?.let {
                    HttpResponse.ok("")
                } ?: throw BadRequestException("Error set like")
            } ?: throw BadRequestException("Post with this id is absent")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun unsetLike(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val postId = call.parameters["postId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param post id")
            Posts.getPostById(postId)?.let {
                if(PostsLikes.selectPostLike(PostLike(postId, userId)) == null)
                    throw BadRequestException("Like not set")
                if(PostsLikes.deletePostLike(PostLike(postId, userId)) == 1){
                    HttpResponse.ok("")
                } else throw BadRequestException("Error unset like")
            } ?: throw BadRequestException("Post with this id is absent")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getLike(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val postId = call.parameters["postId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param post id")
            Posts.getPostById(postId)?.let {
                if(PostsLikes.selectPostLike(PostLike(postId, userId)) == null)
                    HttpResponse.ok(IsLikeResponse(false))
                else HttpResponse.ok(IsLikeResponse(true))
            } ?: throw BadRequestException("Post with this id is absent")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun createComment(call: ApplicationCall): HttpResponse<Any> {
        return try{
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val postId = call.parameters["postId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param post id")
            val textComment = call.receive<PostCommentRequest>().text
            if(Posts.getPostById(postId) == null) throw BadRequestException("Post with this id is absent")
            PostsComments.createComment(
                Comment(
                    id = 0,
                    userId = userId,
                    postId = postId,
                    text = textComment,
                    date = System.currentTimeMillis()
                )
            )?.let {comment ->
                HttpResponse.ok(comment.toCommentResponse())
            } ?: throw BadRequestException("error create comment")
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun getComments(call: ApplicationCall): HttpResponse<Any> {
        return try{
            val postId = call.parameters["postId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param post id")
            if(Posts.getPostById(postId) == null) throw BadRequestException("Post with this id is absent")
            val list = PostsComments.getCommentsByPostId(postId)
            HttpResponse.ok(CommentsListResponse(list))
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    private fun validatePostFields(postRequest: PostRequest){
        val message = when {
            (postRequest.text.isBlank() || postRequest.title.isBlank()) -> "Fields should not be blank"
            else -> return
        }
        throw BadRequestException(message)
    }
}



interface PostController{
    suspend fun createPost(postRequest: PostRequest, call: ApplicationCall): HttpResponse<Any>
    suspend fun getPostById(call: ApplicationCall): HttpResponse<Any>
    suspend fun getPosts(call: ApplicationCall): HttpResponse<Any>
    suspend fun setLike(call: ApplicationCall): HttpResponse<Any>
    suspend fun unsetLike(call: ApplicationCall): HttpResponse<Any>
    suspend fun getLike(call: ApplicationCall): HttpResponse<Any>
    suspend fun createComment(call: ApplicationCall): HttpResponse<Any>
    suspend fun getComments(call: ApplicationCall): HttpResponse<Any>
}