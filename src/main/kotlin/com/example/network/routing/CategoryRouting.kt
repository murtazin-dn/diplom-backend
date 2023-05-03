package com.example.network.routing

import com.example.controller.CategoriesController
import com.example.network.model.HttpResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureCategoryRouting(){

    val controller by inject<CategoriesController>()

    route("/category"){
        get{
            controller.getCategories().let {response ->
                when(response){
                    is HttpResponse.Error -> call.respond(response.code, response.message)
                    is HttpResponse.Success -> call.respond(response.code, response.body)
                }
            }
        }
        get("/{categoryId}"){
            controller.getCategoryById(call).let {response ->
                when(response){
                    is HttpResponse.Error -> call.respond(response.code, response.message)
                    is HttpResponse.Success -> call.respond(response.code, response.body)
                }
            }
        }
    }
}