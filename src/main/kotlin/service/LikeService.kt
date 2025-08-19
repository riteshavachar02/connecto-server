package com.example.service

import com.example.data.repository.like.LikeRepository
import com.example.data.requests.LikeUpdateRequest

class LikeService(
    private val repository: LikeRepository
) {
    suspend fun likeParent(request: LikeUpdateRequest, userId: String): Boolean {
        return repository.likeParent(
            userId = userId,
            parentId = request.parentId
        )
    }
    suspend fun unlikeParent(request: LikeUpdateRequest, userId: String): Boolean {
        return repository.unLikeParent(
            userId = userId,
            parentId = request.parentId
        )
    }

    suspend fun deleteLikesForParent(parentId: String) {
        repository.deleteLikesForParent(parentId)
    }
}