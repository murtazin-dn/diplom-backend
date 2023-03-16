package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.PostLike
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object PostsLikes: Table("posts_likes") {
    val postId = long("post_id")
    val userId = long("user_id")



    suspend fun insertPostLike(postLike: PostLike): PostLike? = dbQuery{
        val insertStatement = PostsLikes.insert {
            it[postId] = postLike.postId
            it[userId] = postLike.userId
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToPostLike(it) }
    }

    suspend fun selectPostLike(postLike: PostLike): PostLike? = dbQuery {
        PostsLikes.select{
            (postId eq postLike.postId) and
                    (userId eq postLike.userId)
        }.mapNotNull { resultRowToPostLike(it) }
            .singleOrNull()
    }

    suspend fun deletePostLike(postLike: PostLike): Int = dbQuery {
        PostsLikes.deleteWhere {(postId eq postLike.postId) and (userId eq postLike.userId) }
    }

    private fun resultRowToPostLike(row: ResultRow) = PostLike (
        postId = row[postId],
        userId = row[userId]
        )
}