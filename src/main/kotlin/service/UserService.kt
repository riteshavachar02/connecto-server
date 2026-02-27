package com.example.service

import com.example.data.models.User
import com.example.data.repository.follow.FollowRepository
import com.example.data.repository.user.UserRepository
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest
import com.example.data.response.UserSearchResponse

class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {

    suspend fun doseUserWithEmailExist(email: String): Boolean {
        return userRepository.getUserByEmail(email) != null
    }

    suspend fun dosePasswordMatchForUser(request: LoginRequest): Boolean {
        return userRepository.dosePasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }

    suspend fun searchUsers(query: String, currentUserId: String, page: Int, pageSize: Int): List<UserSearchResponse> {
        val users = userRepository.searchUsers(query, page, pageSize)
        return users
            .filter { it.id != currentUserId}
            .map { user ->
            val isFollowing = followRepository.isFollowing(
                followingUserId = currentUserId,
                followedUserId = user.id
            )
            UserSearchResponse(
                userName = user.username,
                profilePictureUrl = user.profileImageUrl,
                bio = user.bio,
                isFollowing = isFollowing
            )
        }
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
                skill = listOf(),
                gitHubUrl = null,
                instagramUrl = null,
                linkedInUrl = null
            )
        )
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