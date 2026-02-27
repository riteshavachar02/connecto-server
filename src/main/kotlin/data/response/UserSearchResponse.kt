package com.example.data.response

data class UserSearchResponse(
    val userName: String,
    val profilePictureUrl: String,
    val bio: String?,
    val isFollowing: Boolean
)
