package com.example.plugins

import com.example.data.repository.user.UserRepository
import com.example.routes.createUserRoute
import com.example.routes.loginUser
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()

    routing {
        createUserRoute(userRepository)
        loginUser(userRepository)
    }
}
