package com.example.data.repository.user

import com.example.data.models.User
import com.example.data.requests.UpdateProfileRequest
import com.example.util.Constants

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun updateUser(
        userId: String,
        profileImageUrl: String,
        updateProfileRequest: UpdateProfileRequest
    ) : Boolean

    suspend fun dosePasswordForUserMatch(email: String, enteredPassword: String): Boolean

    suspend fun doseEmailBelongToUserId(email: String, userId: String): Boolean

    suspend fun deleteUserById(userId: String): Boolean

    suspend fun searchUsers(
        query: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_USER_PAGE_SIZE,
    ): List<User>

    suspend fun getUsers(userIds: List<String>): List<User>

}