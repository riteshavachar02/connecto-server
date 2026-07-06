package com.example.data.repository.like

import com.example.data.models.Like
import com.example.data.util.ParentType
import com.example.util.Constants

interface LikeRepository {

    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean

    suspend fun unLikeParent(userId: String, parentId: String): Boolean

    suspend fun deleteLikesForParent(parentId: String)

    suspend fun getLikesForParent(
        parentId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_LIKE_PAGE_SIZE
    ): List<Like>
}