package com.example.database.model.users

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Friend
import com.example.model.PersonInfo
import com.example.model.PersonInfoWithRequestStatus
import com.example.model.RequestFriend
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object RequestsFriends: Table("requests_friends") {
    val id = long("id").autoIncrement()
    val userId = long("userId")
    val friendId = long("friendId")
    val status = integer("status")

    suspend fun insertRequest(requestFriend: RequestFriend): RequestFriend? = dbQuery {
        val insertStatement = RequestsFriends.insert {
            it[userId] = requestFriend.userId
            it[friendId] = requestFriend.friendId
            it[status] = 0
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToRequestFriend)
    }

    suspend fun getRequest(userId: Long, friendId: Long): RequestFriend? = dbQuery{
        RequestsFriends.select{
            ((RequestsFriends.userId eq userId) and (RequestsFriends.friendId eq friendId)) or
                    ((RequestsFriends.userId eq friendId) and (RequestsFriends.friendId eq userId))
        }.mapNotNull { resultRowToRequestFriend(it) }.singleOrNull()
    }

    suspend fun getRequestById(id: Long): RequestFriend? = dbQuery{
        RequestsFriends.select{
            (RequestsFriends.id eq id)
        }.mapNotNull { resultRowToRequestFriend(it) }.singleOrNull()
    }

    suspend fun acceptRequest(id: Long): Int = dbQuery {
        RequestsFriends.update({ RequestsFriends.id eq id }){
            it[status] = 1
        }
    }

    suspend fun declineRequest(id: Long): Int = dbQuery {
        RequestsFriends.update({ RequestsFriends.id eq id }){
            it[status] = 2
        }
    }

    suspend fun getInputRequestList(id: Long): List<PersonInfoWithRequestStatus> = dbQuery {

        val join1 = Join(Users, RequestsFriends,
            onColumn = Users.id, otherColumn = userId,
            joinType = JoinType.INNER,
            additionalConstraint = { friendId eq id})


        join1.slice(Users.id,
            Users.name,
            Users.surname,
            Users.age,
            Users.categoryId,
            Users.doctorStatus,
            Users.icon,
            RequestsFriends.id,
            status
        ).selectAll().map { resultRowToPersonInfo(it) }

    }

    suspend fun getOutputRequestList(id: Long): List<PersonInfoWithRequestStatus> = dbQuery {

        val join1 = Join(Users, RequestsFriends,
            onColumn = Users.id, otherColumn = friendId,
            joinType = JoinType.INNER,
            additionalConstraint = { userId eq id})


        join1.slice(Users.id,
            Users.name,
            Users.surname,
            Users.age,
            Users.categoryId,
            Users.doctorStatus,
            Users.icon,
            RequestsFriends.id,
            status
        ).selectAll().map { resultRowToPersonInfo(it) }

    }



    private fun resultRowToPersonInfo(row: ResultRow) = PersonInfoWithRequestStatus(
        userId = row[Users.id],
        name = row[Users.name],
        surname = row[Users.surname],
        age = row[Users.age],
        categoryId = row[Users.categoryId],
        doctorStatus = row[Users.doctorStatus],
        icon = row[Users.icon],
        requestId = row[id],
        requestStatus = row[status]
    )

    private fun resultRowToRequestFriend(row: ResultRow) = RequestFriend(
        id = row[id],
        userId = row[userId],
        friendId = row[friendId],
        status = row[status]
    )
}