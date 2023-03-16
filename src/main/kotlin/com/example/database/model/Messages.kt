package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Message
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Messages : Table("messages") {
    val id = long("id").autoIncrement()
    val chatId = long("chat_id")
    val userId = long("user_id")
    val text = text("text")
    val date = timestamp("date")

    suspend fun createMessage(message: Message): Message? = dbQuery{
        val insertStatement = Messages.insert {
            it[chatId] = message.chatId
            it[userId] = message.userId
            it[text] = message.text
            it[date] = Instant.ofEpochSecond(message.date)
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToMessage(it) }
    }

    suspend fun getMessageById(id: Long): Message? = dbQuery {
        Messages.select{
            (Messages.id eq id)
        }.mapNotNull { resultRowToMessage(it) }.singleOrNull()
    }

    suspend fun getLastMessageByChatId(chatId: Long): Message? = dbQuery {
        Messages.select {
            (Messages.chatId eq chatId)
        }.orderBy(date to SortOrder.DESC)
            .firstNotNullOfOrNull { resultRowToMessage(it) }
    }

    suspend fun getMessagesByChatId(chatId: Long): List<Message> = dbQuery {
        Messages.select{
            (Messages.chatId eq chatId)
        }.orderBy( date to SortOrder.DESC)
            .map{ resultRowToMessage(it) }

    }


    private fun resultRowToMessage(row: ResultRow) = Message(
        id = row[id],
        userId = row[userId],
        chatId = row[chatId],
        text = row[text],
        date = row[date].toEpochMilli()
    )
}