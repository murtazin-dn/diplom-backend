package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Posts : Table("posts") {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val title = varchar("title", 200)
    val text = text("text")
    val categoryId = long("category_id")
    val timeAtCreation = timestamp("time_creation")
    val likesCount = long("likes_count")
    val commentsCount = long("comments_count")


    suspend fun insertPost(post: Post): Post? = dbQuery {
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

    suspend fun getPostById(id: Long): PostInfo? = dbQuery {
        Join(
            Posts, Users,
            onColumn = Posts.userId, otherColumn = Users.id,
            joinType = JoinType.INNER
        )
            .join(
                Categories, JoinType.INNER,
                onColumn = Posts.categoryId, otherColumn = Categories.id,
            ).select(Posts.id eq id).mapNotNull {
                val user = UserInfo(
                    id = it[Users.id],
                    name = it[Users.name],
                    surname = it[Users.surname],
                    icon = it[Users.icon],
                    doctorStatus = it[Users.doctorStatus],
                    dateOfBirthday = it[Users.dateOfBirthday].toEpochMilli(),
                    category = Category(
                        it[Users.categoryId],
                        Categories.getCategoryById(it[Users.categoryId])!!.name
                    )
                )
                PostInfo(
                    id = it[Posts.id],
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
                    isLikeEnabled = false,
                    images = mutableListOf()
                )
            }.singleOrNull()
    }

    suspend fun deletePostById(id: Long): Int = dbQuery {
        Posts.deleteWhere {
            (Posts.id eq id)
        }
    }


    suspend fun getPostsSubscribers(userId: Long): List<PostInfo> = dbQuery {
        Join(
            Posts, Subscribers,
            onColumn = Posts.userId, otherColumn = Subscribers.subscriberId,
            joinType = JoinType.INNER,
            additionalConstraint = { Subscribers.userId eq userId })
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
                    dateOfBirthday = it[Users.dateOfBirthday].toEpochMilli(),
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
                    isLikeEnabled = false,
                    images = mutableListOf()
                )
            }
    }

//    suspend fun getPostsByUserId(userId: Long): List<PostInfo> = dbQuery{
//        val postCategory = Categories.alias("post_category")
//        val userCategory = Categories.alias("user_category")
//        Join(
//            Posts, Users,
//            onColumn = Posts.userId, otherColumn = Users.id,
//            joinType = JoinType.INNER)
//            .join(
//                Categories, JoinType.INNER,
//                onColumn = Posts.categoryId, otherColumn = Categories.id,
//            ).join(
//                userCategory, JoinType.INNER,
//                onColumn = Users.categoryId, otherColumn = Categories.id,
//            ).select(Posts.userId eq userId).mapNotNull {
//                val user = UserInfo(
//                    id = it[Users.id],
//                    name = it[Users.name],
//                    surname = it[Users.surname],
//                    icon = it[Users.icon],
//                    doctorStatus = it[Users.doctorStatus],
//                    dateOfBirthday = it[Users.dateOfBirthday].toEpochMilli(),
//                    category = Category(
//                        it[Users.categoryId],
//                        it[userCategory[Categories.name]]
//                    )
//                )
//                PostInfo(
//                    id = it[id],
//                    user = user,
//                    title = it[title],
//                    text = it[text],
//                    category = Category(
//                        it[categoryId],
//                        it[Categories.name]
//                    ),
//                    timeAtCreation = it[timeAtCreation].toEpochMilli(),
//                    likesCount = it[likesCount],
//                    commentsCount = it[commentsCount],
//                    isLikeEnabled = false
//                )
//            }
//    }

    suspend fun getPostsByUserId(userId: Long): List<PostInfo> = dbQuery {
        Join(
            Posts, Users,
            onColumn = Posts.userId, otherColumn = Users.id,
            joinType = JoinType.INNER
        )
            .join(
                Categories, JoinType.INNER,
                onColumn = Posts.categoryId, otherColumn = Categories.id,
            ).select(Posts.userId eq userId).orderBy(likesCount to SortOrder.DESC).mapNotNull {
                val user = UserInfo(
                    id = it[Users.id],
                    name = it[Users.name],
                    surname = it[Users.surname],
                    icon = it[Users.icon],
                    doctorStatus = it[Users.doctorStatus],
                    dateOfBirthday = it[Users.dateOfBirthday].toEpochMilli(),
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
                    isLikeEnabled = false,
                    images = mutableListOf()
                )
            }
    }

    suspend fun getPosts(userid: Long): List<PostInfo> = dbQuery {
        val postsMap: MutableMap<Long, PostInfo> = HashMap()
        val postsList = mutableListOf<PostInfo>()
        Join(
            Posts, Users,
            onColumn = Posts.userId, otherColumn = Users.id,
            joinType = JoinType.INNER
        )
            .join(
                Categories, JoinType.INNER,
                onColumn = Posts.categoryId, otherColumn = Categories.id,
            )
            .join(
                PostImages, JoinType.LEFT,
                onColumn = Posts.id, otherColumn = PostImages.postId
            )
            .leftJoin(PostsLikes, { Posts.id }, { PostsLikes.postId }, { PostsLikes.userId eq userid })
            .slice(
                Users.id,
                Users.name,
                Users.surname,
                Users.icon,
                Users.doctorStatus,
                Users.dateOfBirthday,
                categoryId,
                Categories.name,
                id,
                title,
                text,
                timeAtCreation,
                likesCount,
                commentsCount,
                PostsLikes.postId,
                PostsLikes.userId,
                PostImages.imageName
//                likes
            )
            .selectAll().orderBy(likesCount to SortOrder.DESC).mapNotNull {
                println(it[PostsLikes.postId].toString())
                val user = UserInfo(
                    id = it[Users.id],
                    name = it[Users.name],
                    surname = it[Users.surname],
                    icon = it[Users.icon],
                    doctorStatus = it[Users.doctorStatus],
                    dateOfBirthday = it[Users.dateOfBirthday].toEpochMilli(),
                    category = Category(
                        it[categoryId],
                        it[Categories.name]
                    )
                )
                val post = PostInfo(
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
                    isLikeEnabled = it[PostsLikes.postId] != null,
                    images = mutableListOf()
                )
                if (postsMap.contains(it[id])) {
                    it[PostImages.imageName]?.let { image ->
                        postsMap[it[id]]!!.images.add(image)
                    }
                } else {
                    it[PostImages.imageName]?.let { image ->
                        post.images.add(image)
                    }
                    postsMap[it[id]] = post
                }
            }
        postsMap.forEach {
            postsList.add(it.value)
        }
        postsList
    }

//    suspend fun getPosts(): List<PostInfo> = dbQuery {
//        Join(
//            Posts, Users,
//            onColumn = Posts.userId, otherColumn = Users.id,
//            joinType = JoinType.INNER
//        )
//            .join(
//                Categories, JoinType.INNER,
//                onColumn = Posts.categoryId, otherColumn = Categories.id,
//            ).selectAll().orderBy(likesCount to SortOrder.DESC).mapNotNull {
//                val user = UserInfo(
//                    id = it[Users.id],
//                    name = it[Users.name],
//                    surname = it[Users.surname],
//                    icon = it[Users.icon],
//                    doctorStatus = it[Users.doctorStatus],
//                    dateOfBirthday = it[Users.dateOfBirthday].toEpochMilli(),
//                    category = Category(
//                        it[Users.categoryId],
//                        Categories.getCategoryById(it[Users.categoryId])!!.name
//                    )
//                )
//                PostInfo(
//                    id = it[id],
//                    user = user,
//                    title = it[title],
//                    text = it[text],
//                    category = Category(
//                        it[categoryId],
//                        it[Categories.name]
//                    ),
//                    timeAtCreation = it[timeAtCreation].toEpochMilli(),
//                    likesCount = it[likesCount],
//                    commentsCount = it[commentsCount],
//                    isLikeEnabled = false,
//                    images = mutableListOf()
//                )
//            }
//    }

    suspend fun getPostsByCategoryId(categoryId: Long): List<PostInfo> = dbQuery {
        Join(
            Posts, Users,
            onColumn = Posts.userId, otherColumn = Users.id,
            joinType = JoinType.INNER
        )
            .join(
                Categories, JoinType.INNER,
                onColumn = Posts.categoryId, otherColumn = Categories.id,
            ).select(Posts.categoryId eq categoryId).mapNotNull {
                val user = UserInfo(
                    id = it[Users.id],
                    name = it[Users.name],
                    surname = it[Users.surname],
                    icon = it[Users.icon],
                    doctorStatus = it[Users.doctorStatus],
                    dateOfBirthday = it[Users.dateOfBirthday].toEpochMilli(),
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
                        it[Posts.categoryId],
                        it[Categories.name]
                    ),
                    timeAtCreation = it[timeAtCreation].toEpochMilli(),
                    likesCount = it[likesCount],
                    commentsCount = it[commentsCount],
                    isLikeEnabled = false,
                    images = mutableListOf()
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

