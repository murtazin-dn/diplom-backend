package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.FCMToken
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object FCMTokens : Table("fcm_tokens") {
    val userId = long("user_id")
    val token = varchar("token", 255)

    suspend fun insertToken(fcmToken: FCMToken): FCMToken? = dbQuery {
        val insertStatement = FCMTokens.insert {
            it[userId] = fcmToken.userId
            it[token] = fcmToken.token
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToFCMToken(it) }
    }

    suspend fun deleteToken(token: String): Int = dbQuery {
        FCMTokens.deleteWhere { FCMTokens.token eq token }
    }

    suspend fun getTokensByUserId(id: Long): List<String> = dbQuery {
        FCMTokens.select { userId eq id }.mapNotNull {
            resultRowToFCMToken(it).token
        }
    }


    private fun resultRowToFCMToken(row: ResultRow) = FCMToken(
        userId = row[userId],
        token = row[token]
    )

}