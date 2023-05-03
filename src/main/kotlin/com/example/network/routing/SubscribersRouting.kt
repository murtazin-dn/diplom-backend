package com.example.network.routing

import com.example.controller.SubscribersController
import com.example.network.model.HttpResponse
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.configureSubscribersRouting() {

    val controller by inject<SubscribersController>()

        route("/subscribers") {
            get("/list"){
                controller.getSubscribers(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
            post("/{userId}") {
                controller.subscribe(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }
            delete("/{userId}") {
                controller.unsubscribe(call).let {response ->
                    when(response){
                        is HttpResponse.Error -> call.respond(response.code, response.message)
                        is HttpResponse.Success -> call.respond(response.code, response.body)
                    }
                }
            }


    }

}