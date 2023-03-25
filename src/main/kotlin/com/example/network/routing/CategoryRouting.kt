package com.example.network.routing

import com.example.controller.CategoriesController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureCategoryRouting(){

    val controller by inject<CategoriesController>()

    route("/category"){
        get{
            val response = controller.getCategories()
            call.respond(response.code, response.body)
        }
        get("/{categoryId}"){
            val response = controller.getCategoryById(call)
            call.respond(response.code, response.body)
        }
    }
}