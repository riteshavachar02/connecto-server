package com.example.routes

import com.example.data.requests.CreatePostRequest
import com.example.data.requests.DeletePostRequest
import com.example.data.response.BasicApiResponse
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.ApiResponseMessage
import com.example.util.Constants
import com.example.util.QueryParams
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPostRoute(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        post("/api/post/create") {
            val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(successful = false, message = "Invalid Request")
                )
                return@post
            }

            val email = call.principal<JWTPrincipal>()?.getClaim("email", String:: class)
            val isEmailByUser = userService.doseEmailBelongToUserId(
                email = email ?: "",
                userId = request.userId
            )
            if (!isEmailByUser) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "You are not who you say you are."
                )
                return@post
            }

            val didUserExist = postService.createPostIfUserExist(request)
            if (!didUserExist) {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = false,
                        message = ApiResponseMessage.USER_NOT_FOUND
                    )
                )
            } else {
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true
                    )
                )
            }
        }
    }
}

fun Route.getPostForFollows(
    postService: PostService,
    userService: UserService
) {
    authenticate {
        get("api/post/get") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val email = call.principal<JWTPrincipal>()?.getClaim("email", String:: class)
            val isEmailByUser = userService.doseEmailBelongToUserId(
                email = email ?: "",
                userId = userId
            )
            if (!isEmailByUser) {
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = "You are not who you say you are."
                )
                return@get
            }
            val posts = postService.getPostsForFollows(userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.deletePost(
    postService: PostService,
) {
    authenticate {
        delete("api/post/delete") {
            val request = call.receiveNullable<DeletePostRequest>() ?: kotlin.run {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BasicApiResponse(successful = false, message = "Invalid Request")
                )
                return@delete
            }
            val post = postService.getPost(request.postId)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            postService.deletePost(request.postId)
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = true,
                    message = "Post deleted Successfully."
                )
            )

        }
    }
}