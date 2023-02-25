package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.time.LocalDateTime

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    //    fun loadPosts() {
//        thread {
//            // Начинаем загрузку
//            _data.postValue(FeedModel(loading = true))
//            try {
//                // Данные успешно получены
//                val posts = repository.getAll()
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (e: IOException) {
//                // Получена ошибка
//                FeedModel(error = true)
//            }.also(_data::postValue)
//        }
//    }
    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        }
        )
    }

    fun save() {
        edited.value?.let {


            repository.save(it, object : PostRepository.GetCallback<Post> {
                override fun onSuccess(post: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _postCreated.postValue(Unit)
                    _data.postValue(
                        FeedModel(
                            posts = _data.value!!.posts,
                            errorText = "Не удалось создать пост"
                        )
                    )
                }
            }
            )
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }


    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun modLike(id: Long, post: Post) {


        val posts = _data.value?.posts?.map {
            if (it.id != id) it
            else {
                it.copy(likes = post.likes, likedByMe = post.likedByMe)
            }
        }
        _data.postValue(FeedModel(posts = posts))

    }


    fun likeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.value?.posts?.map {
            if (it.id == id) {
                if (it.likedByMe) {
                    repository.unlikeById(id, object : PostRepository.GetCallback<Post> {
                        override fun onSuccess(post: Post) {
                            modLike(id, post)
                        }

                        override fun onError(e: Exception) {
                            _data.postValue(
                                FeedModel(
                                    posts = old,
                                    errorText = "Не удалось снять лайк"
                                )
                            )
                        }
                    }
                    )

                } else {
                    repository.likeById(id, object : PostRepository.GetCallback<Post> {
                        override fun onSuccess(post: Post) {
                            modLike(id, post)
                        }

                        override fun onError(e: Exception) {
                            _data.postValue(
                                FeedModel(
                                    posts = old,
                                    errorText = "Не удалось поставить лайк"
                                )
                            )

                        }
                    }
                    )
                }
            }
        }


//        thread {
////            var post: Post
//            try {
//
//                _data.postValue(_data.value?.posts?.let { posts ->
//                    _data.value?.copy(posts = posts.map {
//                        if (it.id != id) it
//                        else {
////                            val inc = if (it.likedByMe) -1 else 1
//                            val post = if (it.likedByMe) repository.unlikeById(id)
//                            else repository.likeById(id)
//
//
//                            // it.copy(likes = it.likes + inc, likedByMe = it.likedByMe)
//                            it.copy(likes = post.likes, likedByMe = post.likedByMe)
//                        }
//                    }
//                    )
//                }
//                )
//
//
//            } catch (e: IOException) {
//
//            }
//
//
//        }


    }

//    fun removeById(id: Long) {
//        thread {
//            // Оптимистичная модель
//            val old = _data.value?.posts.orEmpty()
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .filter { it.id != id }
//                )
//            )
//            try {
//                repository.removeById(id)
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        }
//    }


    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()

        repository.removeById(id, object : PostRepository.GetCallback<Unit> {
            override fun onSuccess(post: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(posts = old, errorText = "Не удалось удалить пост"))
            }
        }
        )
    }
}
