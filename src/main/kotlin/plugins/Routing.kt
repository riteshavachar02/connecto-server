package com.example.plugins

import com.example.routes.createComment
import com.example.routes.createPost
import com.example.routes.createUser
import com.example.routes.deleteComment
import com.example.routes.deletePost
import com.example.routes.deleteUser
import com.example.routes.followUser
import com.example.routes.getActivities
import com.example.routes.getCommentsForPost
import com.example.routes.getPostForFollows
import com.example.routes.likeParent
import com.example.routes.loginUser
import com.example.routes.unfollowUser
import com.example.routes.unlikeParent
import com.example.service.ActivityService
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
    val activityService: ActivityService by inject()

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
        deleteUser(userService)

        //Follow Routes
        followUser(followService, activityService)
        unfollowUser(followService, activityService)

        // Post Routes
        createPost(postService)
        getPostForFollows(postService)
        deletePost(postService, likeService, commentService)

        //Like Routes
        likeParent(likeService, activityService)
        unlikeParent(likeService, activityService)

        // Comment Routes
        createComment(commentService, activityService)
        deleteComment(commentService, likeService, activityService)
        getCommentsForPost(commentService)

        //Activity Route
        getActivities(activityService)
    }
}
