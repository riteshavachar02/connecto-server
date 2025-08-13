package com.example.data.repository.post

import com.example.data.models.Following
import com.example.data.models.Post
import com.example.data.models.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class PostRepositoryImpl(
    db: CoroutineDatabase
): PostRepository {

    private val posts = db.getCollection<Post>()
    private val following = db.getCollection<Following>()
    private val users = db.getCollection<User>()

    override suspend fun createPostIfUserExists(post: Post): Boolean {
        val doseUserExist = users.findOneById(post.userId) != null
        if (!doseUserExist){
            return false
        }
        posts.insertOne(post)
        return true
    }

    override suspend fun deletePost(postId: String) {
        posts.deleteOneById(postId)
    }

    override suspend fun getPostsByFollows(
        userId: String,
        page: Int,
        pageSize: Int
    ): List<Post> {
        val userIdsFromFollows = following.find(filter = Following::followingUserId eq userId)
            .toList()
            .map { it.followedUserId }

    return posts.find(Post::userId `in` userIdsFromFollows)
        .skip(page * pageSize)
        .limit(pageSize)
        .descendingSort(Post::timestamp)
        .toList()
    }

    override suspend fun getPost(postId: String): Post? {
        return posts.findOneById(postId)
    }
}