package com.example.service

import com.example.data.models.Comment
import com.example.data.repository.comment.CommentRepository
import com.example.data.requests.CreateCommentRequest
import com.example.util.Constants

class CommentService(
    private val repository: CommentRepository
) {
    suspend fun createComment(createCommentRequest: CreateCommentRequest): ValidationEvent {
        createCommentRequest.apply {
            if (comment.isBlank() || userId.isBlank() || postId.isBlank()) {
                return ValidationEvent.ErrorFieldsEmpty
            }
            if (comment.length > Constants.MAX_COMMENT_LENGTH) {
                return ValidationEvent.ErrorCommentTooLong
            }
        }

        repository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                userId = createCommentRequest.userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis()
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteComment(commentId: String) : Boolean {
        return repository.deleteComment(commentId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment> {
        return repository.getCommentsForPost(postId)
    }

    suspend fun getComment(commentId: String) {
        repository.getComment(commentId)
    }

    sealed class ValidationEvent {
        object ErrorFieldsEmpty: ValidationEvent()
        object ErrorCommentTooLong: ValidationEvent()
        object Success: ValidationEvent()
    }
}