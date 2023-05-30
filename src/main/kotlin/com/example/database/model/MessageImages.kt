package com.example.database.model

import com.example.database.DatabaseFactory
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object MessageImages : Table("message_images") {
    val messageId = long("message_id")
    val imageName = varchar("image_name", 200).nullable()

    suspend fun addImagesToMessage(id: Long, images: List<String>) = DatabaseFactory.dbQuery {
        val list = mutableListOf<String>()
        for (image in images) {
            val insertStatement = MessageImages.insert {
                it[messageId] = id
                it[imageName] = image
            }
            insertStatement.resultedValues?.singleOrNull()?.let { list.add(resultRowToMessageImageName(it)!!) }
        }
        return@dbQuery list
    }

    suspend fun getImagesByMessageId(id: Long): List<String> = DatabaseFactory.dbQuery {
        MessageImages.select { messageId eq id }.mapNotNull { resultRowToMessageImageName(it) }
    }


    //    private fun resultRowToPostImage(row: ResultRow) = PostImage(
//        postId = row[postId],
//        imageName = row[imageName]
//    )
    private fun resultRowToMessageImageName(row: ResultRow) = row[imageName]
}