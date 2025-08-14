package com.example.routes

import com.example.data.requests.CreateCommentRequest
import com.example.data.requests.DeleteCommentRequest
import com.example.data.response.BasicApiResponse
import com.example.service.CommentService
import com.example.service.LikeService
import com.example.util.ApiResponseMessage
import com.example.util.QueryParams
import io.ktor.client.request.request
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.createComment(
    commentService: CommentService
) {
    authenticate {
        post("/api/comment/create") {
            val request = call.receiveNullable<CreateCommentRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(
                        successful = false,
                        message = "Invalid Request"
                    )
                )
                return@post
            }

            when(commentService.createComment(request)) {
                is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse(
                            successful = false,
                            message = ApiResponseMessage.FIELDS_BLANK
                        )
                    )
                }
                is CommentService.ValidationEvent.ErrorFieldsEmpty -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse(
                            successful = false,
                            message = ApiResponseMessage.COMMENT_TOO_LONG
                        )
                    )
                }
                is CommentService.ValidationEvent.Success -> {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse(
                            successful = true,
                            message = ApiResponseMessage.COMMENT_CREATED_SUCCESSFULLY
                        )
                    )
                }
            }
        }
    }
}

fun Route.getCommentsForPost(
    commentService: CommentService
) {
    authenticate {
        get("/api/comment/get") {
            val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(
                        successful = false,
                        message = "Invalid Request"
                    )
                )
                return@get
            }

            val comments = commentService.getCommentsForPost(postId)
            call.respond(
                status = HttpStatusCode.OK,
                message = comments
            )
        }
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
) {
    authenticate {
        delete("/api/comment/delete") {
            val request = call.receiveNullable<DeleteCommentRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(
                        successful = false,
                        message = "Invalid Request"
                    )
                )
                return@delete
            }

            val delete = commentService.deleteComment(request.commentId)
            if (delete) {
                likeService.deleteLikesForParent(request.commentId)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true,
                        message = ApiResponseMessage.COMMENT_DELETED_SUCCESSFULLY
                    )
                )
            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessage.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}