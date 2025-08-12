package com.example.service

import com.example.data.repository.follow.FollowRepository
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.FollowUpdateRequest

class FollowService(
   private val repository: FollowRepository
) {

    suspend fun followUserIfExist(request: FollowUpdateRequest): Boolean {
        return repository.followUserIfExist(
            followingUserId = request.followingUserId,
            followedUserId = request.followedUserId
        )
    }

    suspend fun unFollowUserIfExist(request: FollowUpdateRequest): Boolean {
        return repository.unFollowUserIfExist(
            followingUserId = request.followingUserId,
            followedUserId = request.followedUserId
        )
    }
}