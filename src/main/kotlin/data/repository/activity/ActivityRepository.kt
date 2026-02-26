package com.example.data.repository.activity

import com.example.data.models.Activity
import com.example.util.Constants

interface ActivityRepository {

    suspend fun createActivity(activity: Activity)

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<Activity>

    suspend fun deleteActivity(
        byUserId: String,
        parentId: String,
        type: Int
    ): Boolean

}