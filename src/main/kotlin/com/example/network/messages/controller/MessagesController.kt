package com.example.network.messages.controller

import com.example.model.Message
import com.example.network.messages.model.Member
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class MessagesController {
    private val chats = ConcurrentHashMap<Long, MutableList<Member>>()

    fun joinChat(userId: Long, chatId: Long, session: WebSocketServerSession){
        if(!chats.containsKey(chatId)){
            chats[chatId] = mutableListOf(Member(userId, session))
        }else {
            chats[chatId]?.add(Member(userId, session))
        }
    }

    suspend fun sendMessage(message: Message){
        chats[message.chatId]?.forEach { member ->
            member.session.sendSerialized(message)
        }
    }

    suspend fun disconnect(userId: Long, chatId: Long){
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