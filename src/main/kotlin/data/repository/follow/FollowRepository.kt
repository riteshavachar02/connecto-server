package com.example.data.repository.follow

import com.example.data.models.Following

interface FollowRepository {

    suspend fun followUserIfExist(
        followingUserId : String,
        followedUserId : String
    ): Boolean

    suspend fun unFollowUserIfExist(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun isFollowing(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun getFollowsByUser(userId: String): List<Following>

}