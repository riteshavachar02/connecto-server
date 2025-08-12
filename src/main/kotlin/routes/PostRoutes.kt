package com.example.routes

import com.example.data.requests.CreatePostRequest
import com.example.data.response.BasicApiResponse
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.ApiResponseMessage
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
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