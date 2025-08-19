package com.example.service

import com.example.data.repository.follow.FollowRepository
import com.example.data.requests.FollowUpdateRequest

class FollowService(
   private val repository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return repository.followUserIfExist(
            followingUserId = followingUserId,
            followedUserId = request.followedUserId
        )
    }

    suspend fun unFollowUserIfExist(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return repository.unFollowUserIfExist(
            followingUserId = followingUserId,
            followedUserId = request.followedUserId
        )
    }
}