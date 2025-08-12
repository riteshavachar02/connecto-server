package com.example.routes

import com.example.data.repository.user.UserRepository
import com.example.data.models.User
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.response.BasicApiResponse
import com.example.service.UserService
import com.example.util.ApiResponseMessage
import com.example.util.ApiResponseMessage.FIELDS_BLANK
import com.example.util.ApiResponseMessage.SUCCESSFUL_LOGIN
import com.example.util.ApiResponseMessage.USER_ALREADY_EXISTS
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.createUserRoute(userService: UserService) {

    post("/api/user/create") {

        val request = call.receiveNullable<CreateAccountRequest>() ?: run {
            call.respond(
                HttpStatusCode.BadRequest,
                BasicApiResponse(successful = false, message = "Invalid request body")
            )
            return@post
        }
        if (userService.doseUSerWithEmailExist(request.email)) {
            call.respond(
                HttpStatusCode.Conflict,
                BasicApiResponse(successful = false, message = USER_ALREADY_EXISTS)
            )
            return@post
        }

        when(userService.validateCreateAccountRequest(request)) {
            is UserService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    BasicApiResponse(successful = false, message = FIELDS_BLANK)
                )
                return@post
            }
            is UserService.ValidationEvent.Success -> {
                userService.createUser(request)
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(successful = true, message = "User created successfully")
                )
            }
        }
    }
}

fun Route.loginUser(userRepository: UserRepository){

    post("/api/user/login") {

        val request = call.receiveNullable<LoginRequest>() ?: run {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = "Invalid request body"
            )
            return@post
        }

        if (request.email.isBlank() || request.password.isBlank()) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = FIELDS_BLANK
            )
        }

        val isCorrectPassword = userRepository.dosePasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
        if (isCorrectPassword) {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse(
                    successful = true,
                    message = SUCCESSFUL_LOGIN
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
