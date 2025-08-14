package com.example.routes

import com.example.data.requests.LikeUpdateRequest
import com.example.data.response.BasicApiResponse
import com.example.service.LikeService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post

fun Route.likeParent(
    likeService: LikeService
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

            val like = likeService.likeParent(request)
            if (like) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Post Liked"
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
    likeService: LikeService
) {
    authenticate {
        delete("api/unlike") {
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

            val unlike = likeService.unlikeParent(
                request = LikeUpdateRequest(
                    userId = request.userId,
                    parentId = request.parentId
                )
            )
            if (unlike) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = "Post unliked"
                )
            } else {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Could not unlike post"
                )
            }
        }
    }
}