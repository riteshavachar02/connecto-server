package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Activity(
    val byUserId: String,
    val toUserId: String,
    val parentId: String,
    val type: Int,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString()
)
