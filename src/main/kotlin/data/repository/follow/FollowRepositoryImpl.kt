package com.example.data.repository.follow

import com.example.data.models.Following
import com.example.data.models.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class FollowRepositoryImpl(
    db: CoroutineDatabase
) : FollowRepository {

    private val following = db.getCollection<Following>()
    private val user = db.getCollection<User>()

    override suspend fun followUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean {
        val doseFollowingUserExist = user.findOneById(followingUserId) != null
        val doseFollowedUserExist = user.findOneById(followedUserId) != null
        if ( !doseFollowingUserExist || !doseFollowedUserExist) {
            return false
        }
        following.insertOne(
            Following(followingUserId, followedUserId)
        )
        return true
    }

    override suspend fun unFollowUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean {
        val deleteResult = following.deleteOne(
            and(
                Following::followingUserId eq followingUserId,
                Following::followedUserId eq followedUserId
            )
        )
        return deleteResult.deletedCount > 0
    }
}