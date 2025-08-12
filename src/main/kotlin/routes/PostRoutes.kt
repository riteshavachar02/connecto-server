package com.example.routes

import com.example.data.requests.CreatePostRequest
import com.example.data.response.BasicApiResponse
import com.example.service.PostService
import com.example.util.ApiResponseMessage
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createPostRoute(postService: PostService) {
    post("/api/post/create") {
        val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = BasicApiResponse(successful = false, message = "Invalid Request")
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