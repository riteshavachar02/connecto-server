package com.example.plugins

import com.example.data.repository.follow.FollowRepository
import com.example.data.repository.user.UserRepository
import com.example.routes.createUserRoute
import com.example.routes.followUser
import com.example.routes.loginUser
import com.example.routes.unfollowUser
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureRouting() {
    val userRepository: UserRepository by inject()
    val followRepository: FollowRepository by inject()

    routing {
        // User Routes
        createUserRoute(userRepository)
        loginUser(userRepository)

        //Follow Routes
        followUser(followRepository)
        unfollowUser(followRepository)
    }
}
