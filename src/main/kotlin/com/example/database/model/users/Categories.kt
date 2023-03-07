package com.example.database.model.users

import com.example.database.DatabaseFactory
import com.example.model.Category
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

object Categories : Table("categories") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 100)

    suspend fun getCategoriesList(): List<Category> = DatabaseFactory.dbQuery {
        Categories.selectAll().map { resultRowToCategory(it) }

    }

    suspend fun getCategoryById(id: Long): Category? = DatabaseFactory.dbQuery {
        Categories.select {
            (Categories.id eq id)
        }.mapNotNull { resultRowToCategory(it) }
            .singleOrNull()
    }

    private fun resultRowToCategory(row: ResultRow) = Category(
        id = row[id],
        name = row[name]
    )
}