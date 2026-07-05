package com.example.routes

import com.example.data.requests.CreatePostRequest
import com.example.data.requests.DeletePostRequest
import com.example.data.response.BasicApiResponse
import com.example.service.CommentService
import com.example.service.LikeService
import com.example.service.PostService
import com.example.util.ApiResponseMessage
import com.example.util.Constants
import com.example.util.Constants.BASE_URL
import com.example.util.Constants.POSTS_DIRECTORY
import com.example.util.Constants.POSTS_ROUTE
import com.example.util.QueryParams
import com.example.util.save
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.createPost(
    postService: PostService,
) {
    val gson: Gson by inject()
    authenticate {
        post("/api/post/create") {
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        createPostRequest = gson.fromJson(
                            partData.value,
                            CreatePostRequest::class.java
                        )
                    }
                    is PartData.FileItem -> {
                        fileName = partData.save(POSTS_DIRECTORY)
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }
            }

            val postPictureUrl = "$BASE_URL${POSTS_ROUTE}$fileName"
            createPostRequest?.let { request ->
                val createPostAcknowledged = postService.createPost(
                    request = request,
                    userId = call.userId,
                    imageUrl = postPictureUrl
                )
                if (createPostAcknowledged) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse(
                            successful = true,
                            message = ApiResponseMessage.POST_CREATED_SUCCESSFULLY
                        )
                    )
                } else {
                    File("${POSTS_DIRECTORY}$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }

            } ?: kotlin.run {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponseMessage.INVALID_REQUEST
                )
                return@post
            }
        }
    }
}

fun Route.getPostForFollows(
    postService: PostService,
) {
    authenticate {
        get("api/post/get") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForFollows(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
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

            if (post.userId == call.userId) {
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)
                commentService.deleteCommentsFromPost(request.postId)
                call.respond(
                    status = HttpStatusCode.OK,
                    message = BasicApiResponse(
                        successful = true,
                        message = "Post deleted Successfully."
                    )
                )
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}