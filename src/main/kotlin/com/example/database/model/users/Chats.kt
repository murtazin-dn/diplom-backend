package com.example.database.model.users

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Chat
import com.example.model.ChatPreview
import com.example.model.Message
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object Chats: Table("chats") {
    val id = long("id").autoIncrement()
    val firstUserId = long("first_user_id")
    val secondUserId = long("second_user_id")

    suspend fun createChat(chat: Chat): Chat? = dbQuery{
        val insertStatement = Chats.insert {
            it[firstUserId] = chat.firstUserId
            it[secondUserId] = chat.secondUserId
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToChat(it) }
    }

    suspend fun getChatById(id: Long): Chat? = dbQuery {
        Chats.select{( Chats.id eq id )}.mapNotNull { resultRowToChat(it) }.singleOrNull()
    }

    suspend fun getChatByFirstAndSecondUserId(firstId: Long, secondId: Long): Chat? = dbQuery {
        Chats.select{
            ((firstUserId eq firstId) and (secondUserId eq secondId)) or
            ((firstUserId eq secondId) and (secondUserId eq firstId))
        }.mapNotNull { resultRowToChat(it) }.singleOrNull()
    }

    suspend fun getChatListByUser(id: Long): List<Chat> = dbQuery {
        Chats.select{
            (firstUserId eq id) or (secondUserId eq id)
        }.map { resultRowToChat(it) }
    }

    suspend fun getChatListByUserWithUserInfo(id: Long): List<ChatPreview> = dbQuery {
        val list = mutableListOf<ChatPreview>()

        val join1 = Join(Chats, Users,
            onColumn = secondUserId, otherColumn = Users.id,
            joinType = JoinType.INNER,
            additionalConstraint = { firstUserId eq id})
        list.addAll(join1.slice(
            Chats.id,
            Chats.secondUserId,
            Users.name,
            Users.surname,
            Users.icon
        ).selectAll().mapNotNull {
            val lastMessage = Messages.getLastMessageByChatId(it[Chats.id])
            return@mapNotNull ChatPreview(
                chatId = it[Chats.id],
                userId = it[Chats.secondUserId],
                name = it[Users.name],
                surname = it[Users.surname],
                icon = it[Users.icon],
                lastMessageText = lastMessage?.text,
                lastMessageDate = lastMessage?.date,
            )
        })

        val join2 = Join(Chats, Users,
            onColumn = firstUserId, otherColumn = Users.id,
            joinType = JoinType.INNER,
            additionalConstraint = { secondUserId eq id})
        list.addAll(join2.slice(
            Chats.id,
            Chats.firstUserId,
            Users.name,
            Users.surname,
            Users.icon
        ).selectAll().mapNotNull {
            val lastMessage = Messages.getLastMessageByChatId(it[Chats.id])
            return@mapNotNull ChatPreview(
                chatId = it[Chats.id],
                userId = it[Chats.firstUserId],
                name = it[Users.name],
                surname = it[Users.surname],
                icon = it[Users.icon],
                lastMessageText = lastMessage?.text,
                lastMessageDate = lastMessage?.date,
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