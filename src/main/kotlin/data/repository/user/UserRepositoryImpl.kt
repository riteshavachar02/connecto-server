package com.example.data.repository.user

import com.example.data.models.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.regex

class UserRepositoryImpl(
    db: CoroutineDatabase
): UserRepository {

    private val users = db.getCollection<User>()

    override suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    override suspend fun getUserById(id: String): User? {
        return users.findOneById(id)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.findOne(User::email eq email)
    }

    override suspend fun dosePasswordForUserMatch(
        email: String,
        enteredPassword: String
    ): Boolean {
        val user = getUserByEmail(email)
        return user?.password == enteredPassword
    }

    override suspend fun doseEmailBelongToUserId(email: String, userId: String): Boolean {
        return users.findOneById(userId)?.email == email
    }

    override suspend fun deleteUserById(userId: String): Boolean {
        val deleteCount =  users.deleteOneById(userId).deletedCount
        return deleteCount > 0
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        pageSize: Int
    ): List<User> {
        return users.find(
            User::username regex "(?i)$query"
        )
            .skip(page * pageSize)
            .limit(pageSize)
            .toList()
    }
}