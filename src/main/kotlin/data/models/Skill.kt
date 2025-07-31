package com.example.data.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Skill(
    val name: String,
    val iconUrl: String?,
    @BsonId
    val id: String = ObjectId().toString()
)
