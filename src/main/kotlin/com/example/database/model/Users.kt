package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Category
import com.example.model.User
import com.example.model.UserInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object Users: Table("users") {
    val id = long("id").autoIncrement()
    val password = varchar("password", 100)
    val email = varchar("email", 256)
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val dateOfBirthday = timestamp("date_of_birthday")
    val categoryId = long("category_id").references(Categories.id)
    val doctorStatus = bool("doctor_status")
    val icon = text("icon").nullable()

    suspend fun insert(user: User) : User? = dbQuery {
        val insertStatement = Users.insert {
            it[password] = user.password
            it[email] = user.email
            it[name] = user.name
            it[surname] = user.surname
            it[dateOfBirthday] = Instant.ofEpochSecond(user.dateOfBirthday)
            it[categoryId] = user.categoryId
            it[doctorStatus] = false
            it[icon] = user.icon
        }
        insertStatement.resultedValues?.singleOrNull()?.let(Users::resultRowToUser)
    }

    suspend fun updatePhoto(userId: Long, photo: String): Boolean = dbQuery{
        Users.update({ id eq userId }){ it[icon] = photo } > 0
    }

    suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select {
            (Users.email eq email)
        }.mapNotNull { resultRowToUser(it) }
            .singleOrNull()
    }

    suspend fun findUsers(param: String): List<UserInfo> = dbQuery {
        val text = param.lowercase()
        val list = text.split(" ").map { "%$it%" }
        val str = "%$text%"
        val query = if(list.size == 2){
            (((name.lowerCase() like list[0]) and (surname.lowerCase() like list[1])) or
                    ((name.lowerCase() like list[0]) and (surname.lowerCase() like list[1])))
        }else{
            (name.lowerCase() like str) or (surname.lowerCase() like str)
        }
        Join(Users, Categories, onColumn = categoryId, otherColumn = Categories.id)
            .select(query).mapNotNull { resultRowToUserInfo(it) }
    }

    suspend fun getUserById(id: Long): User? = dbQuery {
        Users.select {
            (Users.id eq id)
        }.mapNotNull { resultRowToUser(it) }
            .singleOrNull()
    }

    suspend fun getUserInfo(id: Long): UserInfo? = dbQuery {
        Join(Users, Categories, onColumn = categoryId, otherColumn = Categories.id,
            additionalConstraint = {Users.id eq id})
            .selectAll().mapNotNull { resultRowToUserInfo(it) }
            .singleOrNull()
    }

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[id],
        email = row[email],
        password = row[password],
        name = row[name],
        surname = row[surname],
        dateOfBirthday = row[dateOfBirthday].toEpochMilli(),
        categoryId = row[categoryId],
        doctorStatus = row[doctorStatus],
        icon = row[icon]
    )

    fun resultRowToUserInfo(row: ResultRow) = UserInfo(
        id = row[id],
        name = row[name],
        surname = row[surname],
        icon = row[icon],
        doctorStatus = row[doctorStatus],
        dateOfBirthday = row[dateOfBirthday].toEpochMilli(),
        category = Category(
            row[categoryId],
            row[Categories.name]
        )
    )

}

