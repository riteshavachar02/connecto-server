package com.example.data.repository.user

import com.example.data.models.User

interface UserRepository {

    suspend fun createUser(user: User)

    suspend fun getUserById(id: String): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun dosePasswordForUserMatch(email: String, enteredPassword: String): Boolean

}