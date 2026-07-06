package com.example.data.models

import com.example.data.util.ParentType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Like(
    val userId: String,
    val parentId: String,
    val parentType: Int,
    val timeStamp: Long,
    @BsonId
    val id: String = ObjectId().toString()
)
