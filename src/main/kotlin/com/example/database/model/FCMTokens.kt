package com.example.database.model

import com.example.database.DatabaseFactory
import com.example.database.DatabaseFactory.dbQuery
import com.example.model.FCMToken
import com.example.model.Post
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.Instant

object FCMTokens: Table("fcm_tokens") {
    val userId = long("user_id")
    val token = varchar("token", 255)

    suspend fun insertToken(fcmToken: FCMToken) : FCMToken? = dbQuery {
        val insertStatement = FCMTokens.insert {
            it[userId] = fcmToken.userId
            it[token] = fcmToken.token
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToFCMToken(it) }
    }
    suspend fun getTokensByUserId(id: Long) : List<String> = dbQuery {
        FCMTokens.select { userId eq id }.mapNotNull {
            resultRowToFCMToken(it).token
        }
    }



    private fun resultRowToFCMToken(row: ResultRow) = FCMToken(
        userId = row[userId],
        token = row[token]
    )

}