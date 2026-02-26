package com.example.routes

import com.example.service.ActivityService
import com.example.util.Constants
import com.example.util.QueryParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.getActivities (
    activityService: ActivityService
) {
    authenticate {
        get("/api/activity/get"){

            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?: Constants.DEFAULT_ACTIVITY_PAGE_SIZE

            val activities = activityService.getActivitiesForUser(
                userId = call.userId,
                page = page,
                pageSize = pageSize
            )

            call.respond(
                status = HttpStatusCode.OK,
                message = activities
            )
        }
    }
}