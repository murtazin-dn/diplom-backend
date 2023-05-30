package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object PostImages : Table("post_images") {
    val postId = long("post_id")
    val imageName = varchar("image_name", 200).nullable()

    suspend fun addImagesToPost(id: Long, images: List<String>) = dbQuery {
        val list = mutableListOf<String>()
        for (image in images) {
            val insertStatement = PostImages.insert {
                it[postId] = id
                it[imageName] = image
            }
            insertStatement.resultedValues?.singleOrNull()?.let { list.add(resultRowToPostImageName(it)!!) }
        }
        return@dbQuery list
    }

    suspend fun getImagesByPostId(id: Long): List<String> = dbQuery {
        PostImages.select { postId eq id }.mapNotNull { resultRowToPostImageName(it) }
    }


    //    private fun resultRowToPostImage(row: ResultRow) = PostImage(
//        postId = row[postId],
//        imageName = row[imageName]
//    )
    private fun resultRowToPostImageName(row: ResultRow) = row[imageName]
}