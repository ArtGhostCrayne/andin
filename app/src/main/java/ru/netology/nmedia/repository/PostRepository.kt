package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
//    fun likeById(id: Long): List<Post>

    fun getAllAsync(callback: GetAllCallback)


//    fun likeById(id: Long): Post
    fun likeById(id: Long, callback: GetLikeCallback)

//    fun unlikeById(id: Long): Post
    fun unlikeById(id: Long, callback: GetLikeCallback)
    fun save(post: Post, callback: GetCallback)
//    fun removeById(id: Long)
    fun removeById(id: Long, callback: GetCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface GetCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    interface GetLikeCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
}
