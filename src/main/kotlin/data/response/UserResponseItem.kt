package com.example.data.response

data class UserResponseItem(
    val userId : String,
    val userName: String,
    val profilePictureUrl: String,
    val bio: String?,
    val isFollowing: Boolean
)
