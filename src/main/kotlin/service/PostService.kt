package com.example.service

import com.example.data.models.Post
import com.example.data.repository.post.PostRepository
import com.example.data.requests.CreatePostRequest
import com.example.util.Constants

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

    suspend fun getPostsForFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ) : List<Post> {
        return repository.getPostsByFollows(userId, page, pageSize)
    }

    suspend fun deletePost(postId: String) {
        return repository.deletePost(postId)
    }

    suspend fun getPost(postId: String): Post? {
        return repository.getPost(postId)
    }
}