package com.example.data.repository.like

import com.example.data.models.Like
import com.example.data.models.User
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class LikeRepositoryImpl(
    db: CoroutineDatabase
): LikeRepository {

    private val likes = db.getCollection<Like>()
    private val users = db.getCollection<User>()

    override suspend fun likeParent(userId: String, parentId: String): Boolean {
        val doseUserExist = users.findOneById(userId) != null
        return if (doseUserExist) {
            likes.insertOne(Like(userId, parentId))
            true
        } else {
            false
        }
    }

    override suspend fun unLikeParent(userId: String, parentId: String): Boolean {
        val doseUserExist = users.findOneById(userId) != null
        return if (doseUserExist) {
            likes.deleteOne(
                and(
                    Like::userId eq userId,
                    Like::parentId eq parentId
                )
            )
            true
        } else{
            false
        }
    }

    override suspend fun deleteLikesForParent(parentId: String) {
        likes.deleteMany(Like::parentId eq parentId )
    }
}