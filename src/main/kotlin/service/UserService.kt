package com.example.service

import com.example.data.models.User
import com.example.data.repository.follow.FollowRepository
import com.example.data.repository.user.UserRepository
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.UpdateProfileRequest
import com.example.data.response.ProfileResponse
import com.example.data.response.UserSearchResponse

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {

    suspend fun doseUserWithEmailExist(email: String): Boolean {
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun getUserProfile(userId: String, callerUserId: String): ProfileResponse? {
        val user = userRepository.getUserById(userId) ?: return null
        return ProfileResponse(
            username = user.username,
            bio = user.bio,
            followerCount = user.followerCount,
            followingCount = user.followingCount,
            postCount = user.postCount,
            profilePictureUrl = user.profileImageUrl,
            topSkillsUrl = user.skills,
            gitHubUrl = user.gitHubUrl,
            instagramUrl = user.instagramUrl,
            linkedinUrl = user.linkedInUrl,
            isOwnedProfile = userId == callerUserId,
            isfollowing = if (userId != callerUserId) {
                followRepository.isFollowing(callerUserId, userId)
            } else {
                false
            }
        )

    }

    suspend fun searchUsers(
        query: String,
        currentUserId: String,
        page: Int,
        pageSize: Int
    ): List<UserSearchResponse> {
        val users = userRepository.searchUsers(query, page, pageSize)
        val followsByUser = followRepository.getFollowsByUser(currentUserId)
        return users.map { user ->
            val isFollowing = followsByUser.find { it.followedUserId == user.id } != null
            UserSearchResponse(
                userId = user.id,
                userName = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }.filter { it.userId != currentUserId }
    }

    fun isValidatePassword(enteredPassword: String, actualPassword: String): Boolean {
        return enteredPassword == actualPassword
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.getUserByEmail(email)
    }
    suspend fun createUser(request: CreateAccountRequest) {
        userRepository.createUser(
            User(
                email = request.email,
                username = request.username,
                password = request.password,
                profileImageUrl = "",
                bio = "",
                skills = listOf(),
                gitHubUrl = null,
                instagramUrl = null,
                linkedInUrl = null
            )
        )
    }

    suspend fun updateUser(
        userId: String,
        profileImageUrl: String,
        updateProfileRequest: UpdateProfileRequest
    ): Boolean {
        return userRepository.updateUser(userId, profileImageUrl, updateProfileRequest)
    }

    suspend fun deleteUser(userId: String): Boolean {
        return userRepository.deleteUserById(userId)
    }

    fun validateCreateAccountRequest(request: CreateAccountRequest): ValidationEvent{
        if (request.email.isBlank() || request.username.isBlank() || request.password.isBlank()) {
            return ValidationEvent.ErrorFieldEmpty
        }
        return ValidationEvent.Success
    }

    sealed class ValidationEvent {
        object ErrorFieldEmpty: ValidationEvent()
        object Success: ValidationEvent()
    }

}