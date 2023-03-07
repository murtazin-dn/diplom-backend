package com.example.database.model.users

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Friend
import com.example.model.PersonInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object Friends: Table("friends") {
    val userId = long("user")
    val friendId = long("friend")

    suspend fun addFriends(friend: Friend): Friend? = dbQuery{
        val insertStatement = Friends.insert {
            it[userId] = friend.userId
            it[friendId] = friend.friendId
        }
        insertStatement.resultedValues?.singleOrNull()?.let( ::resultRowToFriends)
    }

    suspend fun getFriend(userId: Long, friendId: Long): Friend? = dbQuery{
        Friends.select{
            ((Friends.userId eq userId) and (Friends.friendId eq friendId)) or
            ((Friends.userId eq friendId) and (Friends.friendId eq userId))
        }.mapNotNull { resultRowToFriends(it) }.singleOrNull()
    }

    suspend fun getFriendsListWithInfoById(id: Long): List<PersonInfo>{
        val list = dbQuery {

            val list = mutableListOf<PersonInfo>()

            val join1 = Join(Users, Friends,
                onColumn = Users.id, otherColumn = userId,
                joinType = JoinType.INNER,
                additionalConstraint = { friendId eq id})
            list.addAll(join1.slice(Users.id,
                Users.name,
                Users.surname,
                Users.age,
                Users.categoryId,
                Users.doctorStatus,
                Users.icon
            ).selectAll().map { resultRowToPersonInfo(it) })

            val join2 = Join(Users, Friends,
                onColumn = Users.id, otherColumn = friendId,
                joinType = JoinType.INNER,
                additionalConstraint = { userId eq id})
            list.addAll(join2.slice(Users.id,
                Users.name,
                Users.surname,
                Users.age,
                Users.categoryId,
                Users.doctorStatus,
                Users.icon
            ).selectAll().map { resultRowToPersonInfo(it) })


            return@dbQuery list
        }

        return list
    }

    suspend fun getFriendsListById(id: Long): List<Friend> = dbQuery {
        Friends.select {
            (userId eq id) or (friendId eq id)
        }.map { resultRowToFriends(it) }
    }

    suspend fun deleteFriend(userId: Long, friendId: Long): Int = dbQuery {
        Friends.deleteWhere {(Friends.userId eq userId) and (Friends.friendId eq friendId)}
    }


    private fun resultRowToPersonInfo(row: ResultRow) = PersonInfo(
        userId = row[Users.id],
        name = row[Users.name],
        surname = row[Users.surname],
        age = row[Users.age],
        categoryId = row[Users.categoryId],
        doctorStatus = row[Users.doctorStatus],
        icon = row[Users.icon]
    )

    private fun resultRowToFriends(row: ResultRow) = Friend(
        userId = row[userId],
        friendId = row[friendId]
    )
}