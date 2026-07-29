package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.requests.UpdateProfileRequest
import com.example.data.response.AuthResponse
import com.example.data.response.BasicApiResponse
import com.example.service.PostService
import com.example.service.UserService
import com.example.util.ApiResponseMessage
import com.example.util.ApiResponseMessage.REGISTER_SUCCESSFUL
import com.example.util.ApiResponseMessage.FIELDS_BLANK
import com.example.util.ApiResponseMessage.USER_ALREADY_EXISTS
import com.example.util.Constants
import com.example.util.Constants.BASE_URL
import com.example.util.Constants.PROFILE_PICTURE_DIRECTORY
import com.example.util.Constants.PROFILE_PICTURE_ROUTE
import com.example.util.QueryParams
import com.example.util.save
import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File
import java.util.*

fun Route.createUser(userService: UserService) {

    post("/api/user/create") {

        val request = call.receiveNullable<CreateAccountRequest>() ?: run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ApiResponseMessage.INVALID_REQUEST
            )
            return@post
        }
        if (userService.doseUserWithEmailExist(request.email)) {
            call.respond(
                BasicApiResponse(successful = false, message = USER_ALREADY_EXISTS)
            )
            return@post
        }

        when(userService.validateCreateAccountRequest(request)) {
            is UserService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                    BasicApiResponse(successful = false, message = FIELDS_BLANK)
                )
                return@post
            }
            is UserService.ValidationEvent.Success -> {
                userService.createUser(request)
                call.respond(
                    BasicApiResponse(successful = true, message = REGISTER_SUCCESSFUL)
                )
            }
        }
    }
}

fun Route.loginUser(
    userService: UserService,
    jwtIssuer: String,
    jwtAudience: String,
    jwtSecret: String
){
    post("/api/user/login") {
        val request = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ApiResponseMessage.INVALID_REQUEST
            )
            return@post
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = FIELDS_BLANK
            )
            return@post
        }


        val user = userService.getUserByEmail(request.email) ?: kotlin.run {
            call.respond(
                status = HttpStatusCode.Unauthorized,
                message = BasicApiResponse(
                    successful = false,
                    message = ApiResponseMessage.INVALID_CREDENTIALS
                )
            )
            return@post
        }
        val isCorrectPassword = userService.isValidatePassword(
            enteredPassword = request.password,
            actualPassword = user.password
        )

        if (isCorrectPassword) {
            val expiresIn = 1000L * 60L * 60L * 24L * 365L
            val token = JWT.create()
                .withClaim("userId", user.id)
                .withIssuer(jwtIssuer)
                .withExpiresAt(Date(System.currentTimeMillis() + expiresIn))
                .withAudience(jwtAudience)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(
                status = HttpStatusCode.OK,
                message = AuthResponse(
                    token = token
                )
            )
        } else {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = false,
                    message = ApiResponseMessage.INVALID_CREDENTIALS
                )
            )
        }
    }
}

fun Route.deleteUser(userService: UserService){

    authenticate {
        delete("/api/user/delete") {

            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)

            if (userId == null ){
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponseMessage.USER_NOT_FOUND
                )
                return@delete
            }

            val isDeleted = userService.deleteUser(userId)

            if (isDeleted){

                call.respond(
                    status = HttpStatusCode.OK,
                    message = ApiResponseMessage.USER_DELETED
                )
            } else {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ApiResponseMessage.USER_NOT_FOUND
                )
            }
        }
    }
}

fun Route.searchUsers(userService: UserService) {
    authenticate {
        get("/api/user/search") {
            val query = call.request.queryParameters[QueryParams.PARAM_QUERY]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 15

            if (query.isNullOrBlank()){
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponseMessage.QUERY_REQUIRED
                )
                return@get
            }

            val currentUserId = call.userId
            val searchResults = userService.searchUsers(
                query = query,
                page = page,
                pageSize = pageSize,
                currentUserId = currentUserId
            )

            call.respond(
                status = HttpStatusCode.OK,
                message = searchResults
            )
        }
    }
}

fun Route.getUserProfile(userService: UserService) {
    authenticate {
        get("/api/user/profile") {
            val userId = call.request.queryParameters[QueryParams.PARAM_USER_ID]

            if (userId.isNullOrBlank()){
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponseMessage.INVALID_REQUEST
                )
                return@get
            }
            val profileResponse = userService.getUserProfile(userId, call.userId)
            if (profileResponse == null){
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponseMessage.USER_NOT_FOUND
                )
                return@get
            }
            call.respond(
                status = HttpStatusCode.OK,
                message = profileResponse
            )
        }
    }
}

fun Route.getPostsForProfile(
    postService: PostService
) {
    authenticate {
        get("/api/user/posts") {
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForProfile(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.updateProfile(userService: UserService) {
    val gson: Gson by inject()
    authenticate {
        put("/api/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest: UpdateProfileRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        updateProfileRequest = gson.fromJson(
                            partData.value,
                            UpdateProfileRequest::class.java
                        )
                    }
                    is PartData.FileItem -> {
                        fileName = partData.save(PROFILE_PICTURE_DIRECTORY)
                    }
                    is PartData.BinaryItem -> Unit
                    is PartData.BinaryChannelItem -> Unit
                }
            }

            val profilePictureUrl = "$BASE_URL${PROFILE_PICTURE_ROUTE}$fileName"
            updateProfileRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = profilePictureUrl,
                    updateProfileRequest = request
                )
                if (updateAcknowledged) {
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = BasicApiResponse(
                            successful = true,
                            message = ApiResponseMessage.USER_PROFILE_UPDATED
                        )
                    )
                } else {
                    File("${PROFILE_PICTURE_DIRECTORY}$fileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }

            } ?: kotlin.run {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponseMessage.INVALID_REQUEST
                )
                return@put
            }

        }
    }
}
