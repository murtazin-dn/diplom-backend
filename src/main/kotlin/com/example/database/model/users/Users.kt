package com.example.database.model.users

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object Users: Table("users") {
    val id = long("id").autoIncrement()
    val login = varchar("login", 30).uniqueIndex()
    val password = varchar("password", 30)
    val email = varchar("email", 30).nullable()
    val name = varchar("name", 100)
    val surname = varchar("surname", 100)
    val age = integer("age")
    val categoryId = long("category_id")
    val doctorStatus = bool("doctor_status")
    val icon = text("icon").nullable()

    suspend fun insert(user: User) : User? = dbQuery {
        val insertStatement = Users.insert {
            it[login] = user.login
            it[password] = user.password
            it[email] = user.email
            it[name] = user.name
            it[surname] = user.surname
            it[age] = user.age
            it[categoryId] = user.categoryId
            it[doctorStatus] = false
            it[icon] = user.icon


        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    suspend fun getUserByLogin(login: String): User? = dbQuery {
        Users.select {
            (Users.login eq login)
        }.mapNotNull { resultRowToUser(it) }
            .singleOrNull()
    }

    suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select {
            (Users.email eq email)
        }.mapNotNull { resultRowToUser(it) }
            .singleOrNull()
    }

    suspend fun getUserById(id: Long): User? = dbQuery {
        Users.select {
            (Users.id eq id)
        }.mapNotNull { resultRowToUser(it) }
            .singleOrNull()
    }

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[id],
        email = row[email],
        login = row[login],
        password = row[password],
        name = row[name],
        surname = row[surname],
        age = row[age],
        categoryId = row[categoryId],
        doctorStatus = row[doctorStatus],
        icon = row[icon]
    )

}

