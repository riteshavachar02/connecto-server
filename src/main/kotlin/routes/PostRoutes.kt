package com.example.routes

import com.example.data.models.Post
import com.example.data.repository.post.PostRepository
import com.example.data.requests.CreatePostRequest
import com.example.data.requests.FollowUpdateRequest
import com.example.data.response.BasicApiResponse
import com.example.util.ApiResponseMessage
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.createPostRoute(postRepository: PostRepository) {
    post("/api/post/create") {
        val request = call.receiveNullable<CreatePostRequest>() ?: kotlin.run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = BasicApiResponse(successful = false, message = "Invalid Request")
            )
            return@post
        }

        val didUserExist = postRepository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = request.userId,
                description = request.description,
                timestamp = System.currentTimeMillis()
            )
        )

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