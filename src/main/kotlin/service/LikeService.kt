package com.example.service

import com.example.data.repository.follow.FollowRepository
import com.example.data.repository.like.LikeRepository
import com.example.data.repository.user.UserRepository
import com.example.data.requests.LikeUpdateRequest
import com.example.data.response.UserResponseItem

class LikeService(
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    suspend fun likeParent(request: LikeUpdateRequest, userId: String): Boolean {
        return likeRepository.likeParent(
            userId = userId,
            parentId = request.parentId,
            parentType = request.parentType
        )
    }
    suspend fun unlikeParent(request: LikeUpdateRequest, userId: String): Boolean {
        return likeRepository.unLikeParent(
            userId = userId,
            parentId = request.parentId
        )
    }

    suspend fun deleteLikesForParent(parentId: String) {
        likeRepository.deleteLikesForParent(parentId)
    }

    suspend fun getUsersWhoLikedParent(parentId: String, userId:String): List<UserResponseItem> {
        val userIds = likeRepository.getLikesForParent(parentId).map { it.userId }
        val users = userRepository.getUsers(userIds)
        val followsByUser = followRepository.getFollowsByUser(userId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserResponseItem(
                userId = user.id,
                userName = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }.filter { it.userId != userId }
    }
}