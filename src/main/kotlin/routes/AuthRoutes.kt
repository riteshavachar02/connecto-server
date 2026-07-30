package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.response.AuthResponse
import com.example.data.response.BasicApiResponse
import com.example.service.UserService
import com.example.util.ApiResponseMessage
import com.example.util.ApiResponseMessage.FIELDS_BLANK
import com.example.util.ApiResponseMessage.REGISTER_SUCCESSFUL
import com.example.util.ApiResponseMessage.USER_ALREADY_EXISTS
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.Date


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
                BasicApiResponse<Unit>(
                    successful = false,
                    message = USER_ALREADY_EXISTS
                )
            )
            return@post
        }

        when(userService.validateCreateAccountRequest(request)) {
            is UserService.ValidationEvent.ErrorFieldEmpty -> {
                call.respond(
                    BasicApiResponse<Unit>(successful = false, message = FIELDS_BLANK)
                )
                return@post
            }
            is UserService.ValidationEvent.Success -> {
                userService.createUser(request)
                call.respond(
                    BasicApiResponse<Unit>(successful = true, message = REGISTER_SUCCESSFUL)
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
                message = BasicApiResponse<Unit>(
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
                message = BasicApiResponse(
                    successful = true,
                    data = AuthResponse(token = token)
                )
            )
        } else {
            call.respond(
                status = HttpStatusCode.OK,
                message = BasicApiResponse<Unit>(
                    successful = false,
                    message = ApiResponseMessage.INVALID_CREDENTIALS
                )
            )
        }
    }
}

fun Route.authenticate() {
    authenticate {
        get ("/api/user/authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}