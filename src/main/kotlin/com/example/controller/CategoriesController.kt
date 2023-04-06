package com.example.controller

import com.example.database.model.Categories
import com.example.network.model.HttpResponse
import com.example.network.model.response.CategoriesResponse
import com.example.network.model.response.CategoryResponse
import com.example.utils.BadRequestException
import com.example.utils.NotFoundException
import io.ktor.server.application.*

class CategoriesControllerImpl: CategoriesController {
    override suspend fun getCategories(): HttpResponse<Any> {
        return try {
            val categories = Categories.getCategoriesList()
            val list = categories.map { category ->
                CategoryResponse(
                    id = category.id,
                    name = category.name
                )
            }
            HttpResponse.ok(list)
        }catch (e: Exception){
            HttpResponse.badRequest(e.message.toString())
        }
    }

    override suspend fun getCategoryById(call: ApplicationCall): HttpResponse<Any> {
        return try {
            val categoryId =
                call.parameters["categoryId"]?.toLongOrNull() ?: throw BadRequestException("invalid param categoryId")
            Categories.getCategoryById(categoryId)?.let { category ->
                HttpResponse.ok(CategoryResponse(category.id, category.name))
            } ?: throw NotFoundException("Category with this id is absent")
        }catch (e: NotFoundException){
            HttpResponse.notFound(e.message)
        }catch (e: BadRequestException){
            HttpResponse.badRequest(e.message)
        }catch (e: Exception){
            HttpResponse.badRequest(e.message.toString())
        }
    }
}

interface CategoriesController {
    suspend fun getCategories() : HttpResponse<Any>
    suspend fun getCategoryById(call: ApplicationCall) : HttpResponse<Any>
}
