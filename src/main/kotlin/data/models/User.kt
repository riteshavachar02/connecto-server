package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: String = ObjectId().toString(),
    val email: String,
    val username: String,
    val password: String,
    val profileImageUrl: String,
    val bio: String,
    val skill: List<String> = listOf(),
    val gitHubUrl: String?,
    val instagramUrl: String?,
    val linkedInUrl: String?,

)
