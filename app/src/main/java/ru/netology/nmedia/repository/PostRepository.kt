package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getAll(): List<Post>
//    fun likeById(id: Long): List<Post>

    fun getAllAsync(callback: GetAllCallback<List<Post>>)

    interface GetAllCallback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }


//    fun likeById(id: Long): Post
    fun likeById(id: Long, callback: GetCallback<Post>)

    interface GetLikeCallback<T> {
        fun onSuccess(post: T) {}
        fun onError(e: Exception) {}
    }

//    fun unlikeById(id: Long): Post
    fun unlikeById(id: Long, callback: GetCallback<Post>)
    fun save(post: Post, callback: GetCallback<Post>)
//    fun removeById(id: Long)
    fun removeById(id: Long, callback: GetCallback<Unit>)



    interface GetCallback<T> {
        fun onSuccess(post: T) {}
        fun onError(e: Exception) {}
    }


}
