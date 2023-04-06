package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant
import javax.jws.soap.SOAPBinding.Use

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
            it[timeAtCreation] = Instant.ofEpochSecond(post.timeAtCreation)
            it[likesCount] = 0
            it[commentsCount] = 0
        }
        insertStatement.resultedValues?.singleOrNull()?.let(Posts::resultRowToPost)
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


    suspend fun getPostsSubscribers(userId: Long): List<PostInfo> = dbQuery{
        Join(
            Posts, Subscribers,
            onColumn = Posts.userId, otherColumn = Subscribers.subscriberId,
            joinType = JoinType.INNER,
            additionalConstraint = { Subscribers.userId eq userId})
            .join(
                Users, JoinType.INNER,
                onColumn = Subscribers.subscriberId, otherColumn = Users.id
            ).join(
                Categories, JoinType.INNER,
                onColumn = Posts.categoryId, otherColumn = Categories.id
            ).selectAll().mapNotNull {
                val user = UserInfo(
                    id = it[Users.id],
                    name = it[Users.name],
                    surname = it[Users.surname],
                    icon = it[Users.icon],
                    doctorStatus = it[Users.doctorStatus],
                    category = Category(
                        it[Users.categoryId],
                        Categories.getCategoryById(it[Users.categoryId])!!.name
                    )
                )
                PostInfo(
                    id = it[id],
                    user = user,
                    title = it[title],
                    text = it[text],
                    category = Category(
                        it[categoryId],
                        it[Categories.name]
                    ),
                    timeAtCreation = it[timeAtCreation].toEpochMilli(),
                    likesCount = it[likesCount],
                    commentsCount = it[commentsCount],
                    isLikeEnabled = false
                )
            }
    }

    suspend fun getPostsByUserId(userId: Long): List<PostInfo> = dbQuery{
        Join(
            Posts, Users,
            onColumn = Posts.userId, otherColumn = Users.id,
            joinType = JoinType.INNER)
            .join(
                Categories, JoinType.INNER,
                onColumn = Posts.categoryId, otherColumn = Categories.id,
            ).select(Posts.userId eq userId).mapNotNull {
                val user = UserInfo(
                    id = it[Users.id],
                    name = it[Users.name],
                    surname = it[Users.surname],
                    icon = it[Users.icon],
                    doctorStatus = it[Users.doctorStatus],
                    category = Category(
                        it[Users.categoryId],
                        Categories.getCategoryById(it[Users.categoryId])!!.name)
                )
                PostInfo(
                    id = it[id],
                    user = user,
                    title = it[title],
                    text = it[text],
                    category = Category(
                        it[categoryId],
                        it[Categories.name]
                    ),
                    timeAtCreation = it[timeAtCreation].toEpochMilli(),
                    likesCount = it[likesCount],
                    commentsCount = it[commentsCount],
                    isLikeEnabled = false
                )
            }
    }




    private fun resultRowToPost(row: ResultRow) = Post(
        id = row[id],
        userId = row[userId],
        title = row[title],
        text = row[text],
        categoryId = row[categoryId],
        timeAtCreation = row[timeAtCreation].toEpochMilli(),
        likesCount = row[likesCount],
        commentsCount = row[commentsCount]
    )

}

enum class PostSort {
    TIME, LIKES
}

