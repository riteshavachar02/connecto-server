package com.example.routes

import com.example.data.models.Activity
import com.example.data.requests.FollowUpdateRequest
import com.example.data.response.BasicApiResponse
import com.example.data.util.ActivityType
import com.example.service.ActivityService
import com.example.service.FollowService
import com.example.util.ApiResponseMessage.USER_NOT_FOUND
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.followUser(
    followService: FollowService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/following/follow") {
            val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(successful = false, message = "Invalid Request")
                )
                return@post
            }

            if (followService.followUserIfExist(request = request, followingUserId = call.userId)) {
                activityService.addFollowActivity(
                    byUserId = call.userId,
                    toUserId = request.followedUserId
                )
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
}

fun Route.unfollowUser(
    followService: FollowService,
    activityService: ActivityService
) {
    authenticate {
        delete("/api/following/unfollow") {

            val request = call.receiveNullable<FollowUpdateRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(successful = false, message = "Invalid Request")
                )
                return@delete
            }

            if (followService.unFollowUserIfExist(request, call.userId)) {
                activityService.deleteFollowActivity(
                    byUserId = call.userId,
                    toUserId = request.followedUserId
                )

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
}
