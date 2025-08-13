package com.example.routes

import com.example.plugins.email
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<Unit, ApplicationCall>.ifEmailBelongsToUser(
    userId: String,
    validationEmail: suspend (email: String, userId: String) -> Boolean,
    onSuccess: () -> Unit
) {
    val isEmailByUser = validationEmail(
        call.principal<JWTPrincipal>()?.email ?: "",
        userId
    )
    if (isEmailByUser) {
        onSuccess()
    } else {
        call.respond(HttpStatusCode.Unauthorized,)
    }
}