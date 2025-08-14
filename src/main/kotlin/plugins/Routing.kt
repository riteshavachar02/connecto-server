package com.example.plugins

import com.example.routes.createComment
import com.example.routes.createPost
import com.example.routes.createUser
import com.example.routes.deleteComment
import com.example.routes.deletePost
import com.example.routes.followUser
import com.example.routes.getCommentsForPost
import com.example.routes.getPostForFollows
import com.example.routes.likeParent
import com.example.routes.loginUser
import com.example.routes.unfollowUser
import com.example.routes.unlikeParent
import com.example.service.CommentService
import com.example.service.FollowService
import com.example.service.LikeService
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
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {

        // User Routes
        createUser(userService)
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
        createPost(postService, userService)
        getPostForFollows(postService, userService)
        deletePost(postService, likeService)

        //Like Routes
        likeParent(likeService)
        unlikeParent(likeService)

        // Comment Routes
        createComment(commentService)
        deleteComment(commentService, likeService)
        getCommentsForPost(commentService)
    }
}
