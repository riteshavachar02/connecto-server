package com.example.repository

import com.example.data.models.User
import com.example.data.repository.user.UserRepository

class FakeUserRepository: UserRepository {

    val users = mutableListOf<User>()

    override suspend fun createUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(id: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByEmail(email: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun dosePasswordForUserMatch(
        email: String,
        enteredPassword: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun doseEmailBelongToUserId(email: String, userId: String): Boolean {
        TODO("Not yet implemented")
    }

}