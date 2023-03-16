package com.example.database.model

import com.example.database.DatabaseFactory.dbQuery
import com.example.model.Subscriber
import com.example.model.UserInfo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object Subscribers: Table("subscribers") {
    val userId = long("user_id")
    val subscriberId = long("subscriber_id")

    suspend fun createSubscribe(subscriber: Subscriber): Subscriber? = dbQuery{
        val insertStatement = Subscribers.insert {
            it[userId] = subscriber.userId
            it[subscriberId] = subscriber.subscriberId
        }
        insertStatement.resultedValues?.singleOrNull()?.let(Subscribers::resulRowToSubscriber)
    }

    suspend fun getSubscribe(userId: Long, subscriberId: Long): Subscriber? = dbQuery{
        Subscribers.select{ (Subscribers.userId eq userId) and (Subscribers.subscriberId eq subscriberId) }
            .mapNotNull { resulRowToSubscriber(it) }.singleOrNull()
    }
    suspend fun getSubscribersByUserId(id: Long): List<UserInfo> = dbQuery{
        val join = Join(Subscribers, Users,
            onColumn = subscriberId, otherColumn = Users.id, joinType = JoinType.INNER,
            additionalConstraint = { userId eq id})
            .join(otherTable = Categories, joinType = JoinType.INNER, onColumn = Users.categoryId,
                otherColumn = Categories.id)

        join.slice(Users.id, Users.name, Users.surname, Users.icon, Users.doctorStatus, Categories.name)
            .selectAll().mapNotNull { Users.resultRowToUserInfo(it) }
    }

    suspend fun deleteSubscribe(userId: Long, subscriberId: Long): Int = dbQuery{
        Subscribers.deleteWhere { (Subscribers.userId eq userId) and (Subscribers.subscriberId eq subscriberId) }
    }



    private fun resulRowToSubscriber(row: ResultRow) = Subscriber(
        userId = row[userId],
        subscriberId = row[subscriberId]
    )
}