package com.example.data.repository.activity

import com.example.data.models.Activity
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.deleteOne
import org.litote.kmongo.eq

class ActivityRepositoryImpl(
    db: CoroutineDatabase
): ActivityRepository {

    private val activities = db.getCollection<Activity>()

    override suspend fun createActivity(activity: Activity) {
         activities.insertOne(activity)
    }

    override suspend fun getActivitiesForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Activity> {
        return activities.find(Activity::toUserId eq userId )
            .skip(page * pageSize)
            .limit(pageSize)
            .descendingSort(Activity::timestamp)
            .toList()
    }

    override suspend fun deleteActivity(
        byUserId: String,
        parentId: String,
        type: Int
    ): Boolean {
        val result = activities.deleteOne(
            and(
                Activity::byUserId eq byUserId,
                Activity::parentId eq  parentId,
                Activity:: type eq type
            )
        )

        return result.deletedCount > 0
    }
}