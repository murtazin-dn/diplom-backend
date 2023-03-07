package com.example.database

import com.example.database.model.users.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val database = Database.connect(
            "jdbc:postgresql://127.0.0.1:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "rootroot")
        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(Posts)
            SchemaUtils.create(Categories)
            SchemaUtils.create(Friends)
            SchemaUtils.create(RequestsFriends)
            SchemaUtils.create(PostsLikes)
            SchemaUtils.create(Chats)
            SchemaUtils.create(Messages)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}