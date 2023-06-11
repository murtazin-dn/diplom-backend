package com.example.database

import com.example.database.model.*
import com.example.model.Message
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
//        val database = Database.connect(
//            "jdbc:postgresql://pg3.sweb.ru:5432/megashlang",
//            driver = "org.postgresql.Driver",
//            user = "megashlang",
//            password = "2QBGKVE^UJWCFm4A"
//        )
        val database = Database.connect(
            "postgresql://containers-us-west-96.railway.app:6472/railway",
            driver = "org.postgresql.Driver",
            user = "postgres"
            password = "cV1mce1BpIitfVqxPm5x"
        )
        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(Posts)
            SchemaUtils.create(Categories)
            SchemaUtils.create(PostsLikes)
            SchemaUtils.create(Chats)
            SchemaUtils.create(Messages)
            SchemaUtils.create(Subscribers)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
