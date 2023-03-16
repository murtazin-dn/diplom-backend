package com.example.controller

import com.example.database.model.Chats
import com.example.database.model.Messages
import com.example.model.Message
import com.example.model.Member
import com.example.network.model.HttpResponse
import com.example.network.model.request.MessageRequest
import com.example.network.model.response.MessageListResponse
import com.example.utils.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import java.util.concurrent.ConcurrentHashMap

class MessagesControllerImpl: MessagesController{

    private val chats = ConcurrentHashMap<Long, MutableList<Member>>()

    override suspend fun getMessages(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)!!
            val chatId = call.parameters["chatId"]?.toLongOrNull() ?: throw BadRequestException("Invalid param chat id")
            val messages = Messages.getMessagesByChatId(chatId).map { it.messageToMessageResponse(userId) }
            HttpResponse.ok(
                MessageListResponse(messages)
            )
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }
    }

    override suspend fun connect(call: ApplicationCall, ws: WebSocketServerSession, incoming: ReceiveChannel<Frame>){
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
                val message = Messages.createMessage(
                    Message(
                        id = 0,
                        chatId = chatId,
                        userId = userId,
                        text = receivedText,
                        date = System.currentTimeMillis()
                    )
                )
                sendMessage(message!!)
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            disconnect(userId, chatId)
        }
    }


    private fun joinChat(userId: Long, chatId: Long, session: WebSocketServerSession){
        if(!chats.containsKey(chatId)){
            chats[chatId] = mutableListOf(Member(userId, session))
        }else {
            chats[chatId]?.add(Member(userId, session))
        }
    }

    private suspend fun sendMessage(message: Message){
        chats[message.chatId]?.forEach { member ->
            member.session.sendSerialized(message.messageToMessageResponse(member.userId))
        }
    }

    private suspend fun disconnect(userId: Long, chatId: Long){
        if(chats[chatId] != null){
            for(member in chats[chatId]!!){
                if (member.userId == userId) member.session.close()
            }
            if(chats[chatId]!!.none()){
                chats.remove(chatId)
            }
        }
    }

}

interface MessagesController {
    suspend fun getMessages(call: ApplicationCall): HttpResponse<Any>
    suspend fun connect(call: ApplicationCall, ws: WebSocketServerSession, incoming: ReceiveChannel<Frame>)
}