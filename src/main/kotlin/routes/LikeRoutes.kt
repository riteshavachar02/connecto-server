package com.example.routes

import com.example.data.requests.LikeUpdateRequest
import com.example.data.response.BasicApiResponse
import com.example.data.util.ParentType
import com.example.service.ActivityService
import com.example.service.LikeService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post

fun Route.likeParent(
    likeService: LikeService,
    activityService: ActivityService
) {
    authenticate {
        post("api/like") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(
                        successful = false,
                        message = "Invalid Request"
                    )
                )
                return@post
            }

            val userId = call.userId
            val parentType = ParentType.formType(request.parentType)
            val message = when(parentType) {
                is ParentType.Post -> "Post liked successfully"
                is ParentType.Comment -> "Comment liked successfully"
                is ParentType.None -> "Like updated"
            }

            val like = likeService.likeParent(request, userId)
            if (like) {
                activityService.addLikeActivity(
                    byUserId = userId,
                    parentId = request.parentId ,
                    parentType = parentType
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true,
                        message = message
                    )
                )
            } else {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Could not like post"
                )
            }
        }
    }
}

fun Route.unlikeParent(
    likeService: LikeService,
    activityService: ActivityService
) {
    authenticate {
        delete("api/like") {
            val request = call.receiveNullable<LikeUpdateRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(
                        successful = false,
                        message = "Invalid Request"
                    )
                )
                return@delete
            }
            val parentType = ParentType.formType(request.parentType)
            val message = when(parentType) {
                is ParentType.Post -> "Post unliked successfully"
                is ParentType.Comment -> "Comment unliked successfully"
                is ParentType.None -> "unLike updated"
            }

            val unlike = likeService.unlikeParent(request, call.userId)
            if (unlike) {
                activityService.deleteLikeActivity(
                    byUserId = call.userId,
                    parentId = request.parentId,
                    parentType = parentType
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true,
                        message = message
                    )
                )
            }
        }
    }
}