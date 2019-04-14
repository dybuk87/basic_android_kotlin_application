package pl.dybuk.health.core.service.chat.impl

import io.reactivex.Observable
import pl.dybuk.health.core.service.chat.dto.CommentDto
import pl.dybuk.health.core.service.chat.dto.PostDto
import pl.dybuk.health.core.service.chat.dto.UserDto
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET

interface ChatServiceRetrofit {

    @GET("users")
    fun getUsers() : Observable<List<UserDto>>

    @GET("posts")
    fun getPosts() : Observable<List<PostDto>>

    @GET("comments")
    fun getComments() : Observable<List<CommentDto>>


    companion object {
        fun create(retrofit: Retrofit) : ChatServiceRetrofit {
            return retrofit.create(ChatServiceRetrofit::class.java)
        }
    }

}