package com.example.service

import com.example.data.models.Activity
import com.example.data.repository.activity.ActivityRepository
import com.example.data.repository.comment.CommentRepository
import com.example.data.repository.post.PostRepository
import com.example.data.util.ActivityType
import com.example.data.util.ParentType
import com.example.routes.userId

class ActivityService(
    private val activityRepository: ActivityRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository

) {

    suspend fun createActivity(activity: Activity){
        activityRepository.createActivity(activity)
    }

    suspend fun addFollowActivity(
        byUserId: String,
        toUserId: String
    ): Boolean {
        if (byUserId == toUserId) {
            return false
        }
        activityRepository.createActivity(
            Activity(
                byUserId = byUserId,
                toUserId = toUserId,
                parentId = toUserId,
                type = ActivityType.FollowedUser.type,
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun addCommentActivity(
        byUserId: String,
        postId: String,
    ): Boolean {
        val userIdOfPost = postRepository.getPost(postId)?.userId ?: return false
        if (byUserId == userIdOfPost) {
            return false
        }
        createActivity(
            Activity(
                byUserId = byUserId,
                toUserId = userIdOfPost,
                parentId = postId,
                type = ActivityType.CommentedOnPost.type,
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun addLikeActivity(
        byUserId: String,
        parentId: String,
        parentType: ParentType
    ): Boolean{
        val toUserId = when(parentType){
            is ParentType.Post -> {
                postRepository.getPost(parentId)?.userId
            }
            is ParentType.Comment -> {
                commentRepository.getComment(parentId)?.userId
            }
            is ParentType.None -> return false

        } ?: return false
        if (byUserId == toUserId){
            return false
        }
        activityRepository.createActivity(
            Activity(
                byUserId = byUserId,
                toUserId = toUserId,
                parentId = parentId,
                type = when(parentType){
                    is ParentType.Post -> ActivityType.LikedPost.type
                    is ParentType.Comment -> ActivityType.LikedComment.type
                    else -> return false
                },
                timestamp = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun deleteLikeActivity(
        byUserId: String,
        parentId: String,
        parentType: ParentType
    ): Boolean {

        val activityType  = when(parentType) {
            is ParentType.Post -> ActivityType.LikedPost.type
            is ParentType.Comment -> ActivityType.LikedComment.type
            is ParentType.None -> return false
        }

        return activityRepository.deleteActivity(
            byUserId = byUserId,
            parentId = parentId,
            type = activityType
        )
    }

    suspend fun deleteCommentActivity(
        byUserId: String,
        postId: String
    ): Boolean {
        return activityRepository.deleteActivity(
            byUserId = byUserId,
            parentId = postId,
            type = ActivityType.CommentedOnPost.type
        )
    }

    suspend fun deleteFollowActivity(
        byUserId: String,
        toUserId: String
    ): Boolean {
        return activityRepository.deleteActivity(
            byUserId = byUserId,
            parentId = toUserId,
            type = ActivityType.FollowedUser.type
        )
    }

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Activity>{
        return activityRepository.getActivitiesForUser(userId, page, pageSize)
    }
}