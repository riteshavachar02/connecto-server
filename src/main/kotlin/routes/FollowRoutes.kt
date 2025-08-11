package com.example.routes

import com.example.data.repository.follow.FollowRepository
import com.example.data.requests.FollowUpdateRequest
import com.example.data.response.BasicApiResponse
import com.example.util.ApiResponseMessage.USER_NOT_FOUND
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post

fun Route.followUser(followRepository: FollowRepository) {
    post("/api/following/follow") {
        val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = BasicApiResponse(successful = false, message = "Invalid Request")
            )
            return@post
        }
        val didUserExist = followRepository.followUserIfExist(
            followingUserId = request.followingUserId,
            followedUserId = request.followedUserId
        )
        if (didUserExist) {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = true
                )
            )
        } else {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}

fun Route.unfollowUser(followRepository: FollowRepository) {
    delete("/api/following/unfollow") {
        // Ensure the body is read as JSON
        val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = BasicApiResponse(successful = false, message = "Invalid Request")
            )
            return@delete
        }

        val didUserExist = followRepository.unFollowUserIfExist(
            followingUserId = request.followingUserId,
            followedUserId = request.followedUserId
        )

        if (didUserExist) {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = true
                )
            )
        } else {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = false,
                    message = USER_NOT_FOUND
                )
            )
        }
    }
}
