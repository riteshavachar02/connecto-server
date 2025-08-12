package com.example.service

import com.example.data.models.Post
import com.example.data.repository.post.PostRepository
import com.example.data.requests.CreatePostRequest

class PostService(
    private val repository: PostRepository
) {
    suspend fun createPostIfUserExist(request: CreatePostRequest): Boolean {
        return repository.createPostIfUserExists(
            Post(
                imageUrl = "",
                userId = request.userId,
                description = request.description,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}