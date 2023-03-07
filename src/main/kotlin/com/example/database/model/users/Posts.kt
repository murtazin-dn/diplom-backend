package com.example.database.model.users

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Post
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.timestamp

object Posts : Table("posts") {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val title = varchar("title", 200)
    val text = text("text")
    val categoryId = long("category_id")
    val timeAtCreation = timestamp("time_creation")
    val likesCount = long("likes_count")
    val commentsCount = long("comments_count")


    suspend fun insertPost(post: Post) : Post? = dbQuery {
        val insertStatement = Posts.insert {
            it[userId] = post.userId
            it[title] = post.title
            it[text] = post.text
            it[categoryId] = post.categoryId
            it[timeAtCreation] = post.timeAtCreation
            it[likesCount] = 0
            it[commentsCount] = 0
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToPost)
    }

    suspend fun getPostById(id: Long): Post? = dbQuery {
        Posts.select {
            (Posts.id eq id)
        }.mapNotNull { resultRowToPost(it) }
            .singleOrNull()
    }

    suspend fun deletePostById(id: Long): Int = dbQuery {
        Posts.deleteWhere {
            (Posts.id eq id)
        }
    }




    private fun resultRowToPost(row: ResultRow) = Post(
        id = row[id],
        userId = row[userId],
        title = row[title],
        text = row[text],
        categoryId = row[categoryId],
        timeAtCreation = row[timeAtCreation],
        likesCount = row[likesCount],
        commentsCount = row[commentsCount]
    )

}

