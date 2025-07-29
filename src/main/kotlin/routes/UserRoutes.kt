package com.example.routes

import com.example.controller.user.UserController
import com.example.data.models.User
import com.example.data.requests.CreateUserAccount
import com.example.data.response.BasicApiResponse
import com.example.util.ApiResponseMessage.FIELDS_BLANK
import com.example.util.ApiResponseMessage.USER_ALREADY_EXISTS
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userController: UserController by application.inject()

    post("/api/user/create") {

        val request = call.receiveNullable<CreateUserAccount>() ?: run {
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(successful = false, message = "Invalid request body")
            )
            return@post
        }

        if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(successful = false, message = FIELDS_BLANK)
            )
            return@post
        }

        val userExists = userController.getUserByEmail(request.email) != null
        if (userExists) {
            call.respond(
                HttpStatusCode.Conflict,
                BasicApiResponse(successful = false, message = USER_ALREADY_EXISTS)
            )
            return@post
        }

        userController.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bio = "",
                skill = listOf(),
                gitHubUrl = null,
                instagramUrl = null,
                linkedInUrl = null
            )
        )
        call.respond(
            HttpStatusCode.OK,
            BasicApiResponse(successful = true, message = "User created successfully")
        )
    }
}
