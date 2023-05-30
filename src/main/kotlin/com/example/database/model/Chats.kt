package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Chat
import com.example.model.ChatPreview
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq

object Chats : Table("chats") {
    val id = long("id").autoIncrement()
    val firstUserId = long("first_user_id")
    val secondUserId = long("second_user_id")

    suspend fun createChat(chat: Chat): Chat? = dbQuery {
        val insertStatement = Chats.insert {
            it[firstUserId] = chat.firstUserId
            it[secondUserId] = chat.secondUserId
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToChat(it) }
    }

    suspend fun getChatById(id: Long): Chat? = dbQuery {
        Chats.select { (Chats.id eq id) }.mapNotNull { resultRowToChat(it) }.singleOrNull()
    }

    suspend fun getChatByFirstAndSecondUserId(firstId: Long, secondId: Long): Chat? = dbQuery {
        Chats.select {
            ((firstUserId eq firstId) and (secondUserId eq secondId)) or
                    ((firstUserId eq secondId) and (secondUserId eq firstId))
        }.mapNotNull { resultRowToChat(it) }.singleOrNull()
    }


    suspend fun getUnreadChatsCount(userId: Long): Long = dbQuery {
        Join(Chats, Messages, JoinType.INNER, Chats.id, Messages.chatId)
            .select { (Messages.userId neq userId) and (Messages.isRead eq false) and ((firstUserId eq userId) or (secondUserId eq userId)) }
            .having { Messages.date eq Messages.date.max() }
            .count()
    }


    suspend fun getChatListByUserWithUserInfo(id: Long): List<ChatPreview> = dbQuery {
        val list = mutableListOf<ChatPreview>()

        val join1 = Join(
            Chats, Users,
            onColumn = secondUserId, otherColumn = Users.id,
            joinType = JoinType.INNER,
            additionalConstraint = { firstUserId eq id })
        list.addAll(join1.selectAll().mapNotNull {
            val lastMessage = Messages.getLastMessageByChatId(it[Chats.id]) ?: return@mapNotNull null
            return@mapNotNull ChatPreview(
                chatId = it[Chats.id],
                userId = it[secondUserId],
                name = it[Users.name],
                surname = it[Users.surname],
                icon = it[Users.icon],
                unreadMessagesCount = Messages.getUnreadMessagesCount(it[Chats.id], id),
                lastMessage = lastMessage.messageToMessageResponse(id)
            )

        })

        val join2 = Join(
            Chats, Users,
            onColumn = firstUserId, otherColumn = Users.id,
            joinType = JoinType.INNER,
            additionalConstraint = { secondUserId eq id })
        list.addAll(join2.selectAll().mapNotNull {
            val lastMessage = Messages.getLastMessageByChatId(it[Chats.id]) ?: return@mapNotNull null
            return@mapNotNull ChatPreview(
                chatId = it[Chats.id],
                userId = it[firstUserId],
                name = it[Users.name],
                surname = it[Users.surname],
                icon = it[Users.icon],
                unreadMessagesCount = Messages.getUnreadMessagesCount(it[Chats.id], id),
                lastMessage = lastMessage.messageToMessageResponse(id)
            )
        })

        return@dbQuery list
    }

    private fun resultRowToChat(row: ResultRow) = Chat(
        id = row[id],
        firstUserId = row[firstUserId],
        secondUserId = row[secondUserId]
    )
}