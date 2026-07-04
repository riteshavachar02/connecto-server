package com.example.data.response

data class ProfileResponse(
    val username: String,
    val bio: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int,
    val profilePictureUrl: String,
    val topSkillsUrl: List<String>,
    val gitHubUrl: String?,
    val instagramUrl: String?,
    val linkedinUrl: String?,
    val isOwnedProfile: Boolean,
    val isfollowing: Boolean,
)
