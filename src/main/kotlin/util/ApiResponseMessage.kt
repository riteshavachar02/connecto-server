package com.example.util

object ApiResponseMessage {
    const val USER_ALREADY_EXISTS = "A user with this email is already exists."
    const val INVALID_CREDENTIALS = "Oops! incorrect credentials, please try again."
    const val FIELDS_BLANK  = "Fields may not be empty."
    const val SUCCESSFUL_LOGIN = "Login successful."
    const val USER_NOT_FOUND = "The user couldn't be Found."
    const val COMMENT_TOO_LONG = "The comment length must not exceed ${Constants.MAX_COMMENT_LENGTH} characters."
    const val COMMENT_CREATED_SUCCESSFULLY = "Comment created successfully."
    const val COMMENT_DELETED_SUCCESSFULLY = "Comment deleted successfully."
}
