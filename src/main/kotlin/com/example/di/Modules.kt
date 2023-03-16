package com.example.di

import com.example.controller.*
import org.koin.dsl.module

val authModule = module {
    single<AuthController> { AuthControllerImpl() }
}
val postModule = module {
    single<PostController> { PostControllerImpl() }
}
val subscribersModule = module {
    single<SubscribersController> { SubscribersControllerImpl() }
}
val userInfoModule = module {
    single<UserInfoController> { UserInfoControllerImpl() }
}
val chatModule = module {
    single<ChatController> { ChatControllerImpl() }
}
val messageModule = module {
    single<MessagesController> { MessagesControllerImpl() }
}