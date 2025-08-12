package com.example.service

import com.example.data.models.User
import com.example.data.repository.user.UserRepository
import com.example.data.requests.CreateAccountRequest
import com.example.data.requests.LoginRequest

class UserService(
    private val repository: UserRepository,
) {

    suspend fun doseUSerWithEmailExist(email: String): Boolean {
        return repository.getUserByEmail(email) != null
    }

    suspend fun dosePasswordMatchForUser(request: LoginRequest): Boolean {
        return repository.dosePasswordForUserMatch(
            email = request.email,
            enteredPassword = request.password
        )
    }

    suspend fun doseEmailBelongToUserId(email: String, userId: String): Boolean {
        return repository.doseEmailBelongToUserId(email, userId)
    }

    suspend fun createUser(request: CreateAccountRequest) {
        repository.createUser(
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