package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Message
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Messages : Table("messages") {
    val id = long("id").autoIncrement()
    val chatId = long("chat_id")
    val userId = long("user_id")
    val text = text("text")
    val date = timestamp("date")
    val isRead = bool("is_read")

    suspend fun createMessage(message: Message): Message? = dbQuery {
        val insertStatement = Messages.insert {
            it[chatId] = message.chatId
            it[userId] = message.userId
            it[text] = message.text
            it[date] = Instant.ofEpochSecond(message.date)
            it[isRead] = false
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToMessage(it) }
    }

    suspend fun readMessage(chatId: Long, messageId: Long, userId: Long): Int = dbQuery {
        val expression: (SqlExpressionBuilder.() -> Op<Boolean>) = {
            (Messages.chatId eq chatId) and (id lessEq messageId) and (Messages.userId neq userId) and (isRead eq false)
        }
        return@dbQuery Messages.update(expression) {
            it[isRead] = true
        }
    }

    suspend fun getMessageById(id: Long): Message? = dbQuery {
        Messages.select {
            (Messages.id eq id)
        }.mapNotNull { resultRowToMessage(it) }.singleOrNull()
    }

    suspend fun getUnreadMessagesCount(chatId: Long, userId: Long): Long = dbQuery {
        Messages
            .select((Messages.chatId eq chatId) and (Messages.userId neq userId) and (Messages.isRead eq false))
            .count()
    }

    suspend fun getLastMessageByChatId(chatId: Long): Message? = dbQuery {
        Messages.select {
            (Messages.chatId eq chatId)
        }.orderBy(date to SortOrder.DESC)
            .firstNotNullOfOrNull { resultRowToMessage(it) }
    }

    suspend fun getMessagesByChatId(chatId: Long): List<Message> = dbQuery {
        val messagesMap: MutableMap<Long, Message> = HashMap()
        val messagesList = mutableListOf<Message>()
        Messages.join(
            MessageImages, JoinType.LEFT,
            onColumn = Messages.id, otherColumn = MessageImages.messageId
        ).select {
            (Messages.chatId eq chatId)
        }.orderBy(date to SortOrder.DESC)
            .mapNotNull {
                val message = resultRowToMessage(it)
                if (messagesMap.contains(it[Messages.id])) {
                    it[MessageImages.imageName]?.let { image ->
                        messagesMap[it[Messages.id]]!!.images.add(image)
                    }
                } else {
                    it[MessageImages.imageName]?.let { image ->
                        message.images.add(image)
                    }
                    messagesMap[it[Messages.id]] = message
                }
            }
        messagesMap.forEach {
            messagesList.add(it.value)
        }
        messagesList
    }

    suspend fun getMessagesFromMessageId(chatId: Long, messageId: Long): List<Message> = dbQuery {
        val messagesMap: MutableMap<Long, Message> = HashMap()
        val messagesList = mutableListOf<Message>()
        Messages.join(
            MessageImages, JoinType.LEFT,
            onColumn = Messages.id, otherColumn = MessageImages.messageId
        ).select {
            ((Messages.chatId eq chatId) and (Messages.id greater messageId))
        }.orderBy(date to SortOrder.DESC)
            .mapNotNull {
                val message = resultRowToMessage(it)
                if (messagesMap.contains(it[Messages.id])) {
                    it[MessageImages.imageName]?.let { image ->
                        messagesMap[it[Messages.id]]!!.images.add(image)
                    }
                } else {
                    it[MessageImages.imageName]?.let { image ->
                        message.images.add(image)
                    }
                    messagesMap[it[Messages.id]] = message
                }
            }
        messagesMap.forEach {
            messagesList.add(it.value)
        }
        messagesList
    }


    private fun resultRowToMessage(row: ResultRow) = Message(
        id = row[id],
        userId = row[userId],
        chatId = row[chatId],
        text = row[text],
        date = row[date].toEpochMilli(),
        isRead = row[isRead],
        images = mutableListOf()
    )
}