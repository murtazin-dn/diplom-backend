package com.example.controller

import com.example.database.model.*
import com.example.model.Member
import com.example.model.Message
import com.example.model.NotificationMessage
import com.example.network.model.HttpResponse
import com.example.network.model.request.MessageRequest
import com.example.network.model.response.ChatResponse
import com.example.network.model.response.MessageResponse
import com.example.utils.BadRequestException
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class MessagesControllerImpl : MessagesController {

    private val chats = ConcurrentHashMap<Long, MutableList<Member>>()

    override suspend fun getMessages(call: ApplicationCall): HttpResponse<List<MessageResponse>> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val chatId = call.parameters["chatId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param chat id")
            val messages = call.request.queryParameters["messageId"]?.toLongOrNull()?.let { messageId ->
                Messages.getMessagesFromMessageId(chatId, messageId).map { it.messageToMessageResponse(userId) }
                    .sortedByDescending { it.date }
            } ?: Messages.getMessagesByChatId(chatId).map { it.messageToMessageResponse(userId) }
                .sortedByDescending { it.date }
            HttpResponse.ok(messages)
        } catch (e: BadRequestException) {
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun readMessage(call: ApplicationCall): HttpResponse<String> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val chatId = call.parameters["chatId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param chat id")
            val messageId = call.parameters["messageId"]?.toLongOrNull()
                ?: throw BadRequestException("param messageId is absent")
            Messages.readMessage(chatId, messageId, userId)
            HttpResponse.ok("")
        } catch (e: BadRequestException) {
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun connect(call: ApplicationCall, ws: WebSocketServerSession, incoming: ReceiveChannel<Frame>) {
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", Long::class)
        val chatId = call.parameters["chatId"]?.toLongOrNull()
            ?: throw BadRequestException("invalid param chatId")
        val chat = Chats.getChatById(chatId)
            ?: throw BadRequestException("Chat with this id is absent")
        if (userId != chat.secondUserId && userId != chat.firstUserId)
            throw BadRequestException("Вы не состоите в этом чате")

        joinChat(userId, chatId, ws)

        try {
            incoming
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                val messageRequest = Json.decodeFromString<MessageRequest>(receivedText)
                val message = Messages.createMessage(
                    Message(
                        id = 0,
                        chatId = chatId,
                        userId = userId,
                        text = messageRequest.text,
                        date = System.currentTimeMillis() / 1000,
                        isRead = false,
                        images = messageRequest.images.toMutableList()
                    )
                )
                MessageImages.addImagesToMessage(message!!.id, messageRequest.images)
                message.images.addAll(messageRequest.images)
                sendMessage(message)
            }
        } catch (e: Exception) {
            println("ddddddddddddddddddddddddddddddddddd")
            println(e.localizedMessage)
            println(e.stackTraceToString())
        } finally {
            println("diiiiiiiiiiiiiiiiisconnect")
            disconnect(userId, chatId)
        }
    }


    private fun joinChat(userId: Long, chatId: Long, session: WebSocketServerSession) {
        if (!chats.containsKey(chatId)) {
            chats[chatId] = mutableListOf(Member(userId, session))
        } else {
            chats[chatId]?.add(Member(userId, session))
        }
    }

    private suspend fun sendMessage(message: Message) {
        chats[message.chatId]?.forEach { member ->
            member.session.sendSerialized(message.messageToMessageResponse(member.userId))

        }
        val chat = Chats.getChatById(message.chatId)
            ?: throw BadRequestException("Chat with this id does not exists")
        val secondUser = Users.getUserInfo(message.userId)!!
        val firstUserId = when {
            (secondUser.id == chat.firstUserId) -> chat.secondUserId
            (secondUser.id == chat.secondUserId) -> chat.firstUserId
            else -> throw BadRequestException("Error")
        } ?: throw BadRequestException("Error")
        val chatResponse = ChatResponse(
            id = chat.id,
            firstUserId = firstUserId,
            secondUser = secondUser.toUserInfoResponse()
        )
        val notificationMessage = NotificationMessage(
            id = message.id,
            chat = chatResponse,
            user = Users.getUserInfo(message.userId)!!.toUserInfoResponse(),
            text = message.text,
            date = message.date
        )

        FCMTokens.getTokensByUserId(firstUserId).forEach { token ->
            try {
                val msgJson = Json.encodeToString(notificationMessage)

                val fcmMessage = com.google.firebase.messaging.Message.builder()
                    .putData("type", "msg")
                    .putData("msg", msgJson)
                    .setToken(token)
                    .build()
                val response: String = FirebaseMessaging.getInstance().send(fcmMessage)
                println("Successfully sent message: $response")
                println("send message to userId $firstUserId")
            } catch (e: Exception) {
                println("Firebase send notification exception ${e.message}")
                println(e.stackTraceToString())
            }
        }

//        val chat = Chats.getChatById(message.chatId)
//            ?: throw BadRequestException("Chat with this id does not exists")
//        val firstUserId = message.userId
//        val secondUser = when{
//            (firstUserId == chat.firstUserId) -> Users.getUserInfo(chat.secondUserId)
//            (firstUserId == chat.secondUserId) -> Users.getUserInfo(chat.firstUserId)
//            else -> throw BadRequestException("Error")
//        } ?: throw BadRequestException("Error")
//        val chatResponse = ChatResponse(
//            id = chat.id,
//            firstUserId = firstUserId,
//            secondUser = secondUser.toUserInfoResponse()
//        )
//        val notificationMessage = NotificationMessage(
//            id = message.id,
//            chat = chatResponse,
//            user = Users.getUserInfo(message.userId)!!.toUserInfoResponse(),
//            text = message.text,
//            date = message.date
//        )
//        FCMTokens.getTokensByUserId(member.userId).forEach { token ->
//            val msgJson = Json.encodeToString(notificationMessage)
//
//            val fcmMessage = com.google.firebase.messaging.Message.builder()
//                .putData("type", "msg")
//                .putData("msg", msgJson)
//                .setToken(token)
//                .build()
//            val response: String = FirebaseMessaging.getInstance().send(fcmMessage)
//            println("Successfully sent message: $response")
//        }
    }


    private suspend fun disconnect(userId: Long, chatId: Long) {
        chats[chatId]?.let {
            for (member in chats[chatId]!!) {
                if (member.userId == userId) {
                    member.session.close()
                    chats[chatId]?.remove(member)
                    println("sessiom close chatId: $chatId, userId: $userId")
                }
            }
            if (chats[chatId]!!.none()) {
                chats.remove(chatId)
                println("remove $chatId")
            }
        }
    }

}

interface MessagesController {
    suspend fun readMessage(call: ApplicationCall): HttpResponse<String>
    suspend fun getMessages(call: ApplicationCall): HttpResponse<List<MessageResponse>>
    suspend fun connect(call: ApplicationCall, ws: WebSocketServerSession, incoming: ReceiveChannel<Frame>)
}