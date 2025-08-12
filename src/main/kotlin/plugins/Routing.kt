package com.example.plugins

import com.example.data.repository.follow.FollowRepository
import com.example.data.repository.post.PostRepository
import com.example.data.repository.user.UserRepository
import com.example.routes.createPostRoute
import com.example.routes.createUserRoute
import com.example.routes.followUser
import com.example.routes.loginUser
import com.example.routes.unfollowUser
import com.example.service.FollowService
import com.example.service.PostService
import com.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.getValue

fun Application.configureRouting() {

    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {

        // User Routes
        createUserRoute(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret
        )

        //Follow Routes
        followUser(followService)
        unfollowUser(followService)

        // Post Routes
        createPostRoute(postService, userService)
    }
}
