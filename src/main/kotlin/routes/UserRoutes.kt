package com.example.routes

import com.example.controller.user.UserController
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
    val userController: UserController by inject()
    route("api/user/create") {
        post {
            val request = call.receiveNullable<CreateUserAccount>() ?: kotlin.run{
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val userExist = userController.getUserByEmail(email = request.email) != null
            if (userExist) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = USER_ALREADY_EXISTS
                    )
                )
                return@post
            }
            if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank() ) {
                call.respond(
                    BasicApiResponse(
                        successful = false,
                        message = FIELDS_BLANK
                    )
                )
                return@post
            }
            call.respond(
                BasicApiResponse(successful = true)
            )
        }
    }
}