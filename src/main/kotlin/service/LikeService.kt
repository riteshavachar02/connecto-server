package com.example.service

import com.example.data.repository.like.LikeRepository
import com.example.data.requests.LikeUpdateRequest

class LikeService(
    private val repository: LikeRepository
) {
    suspend fun likeParent(request: LikeUpdateRequest): Boolean {
        return repository.likeParent(
            userId = request.userId,
            parentId = request.parentId
        )
    }
    suspend fun unlikeParent(request: LikeUpdateRequest): Boolean {
        return repository.unLikeParent(
            userId = request.userId,
            parentId = request.parentId
        )
    }
}