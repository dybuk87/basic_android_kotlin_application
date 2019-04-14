package pl.dybuk.health.core.service.chat

import io.reactivex.Observable
import pl.dybuk.health.core.service.chat.dto.CommentDto
import pl.dybuk.health.core.service.chat.dto.PostDto
import pl.dybuk.health.core.service.chat.dto.UserDto

interface ChatService {
    fun getUsers() : Observable<List<UserDto>>

    fun getPosts() : Observable<List<PostDto>>

    fun getComments() : Observable<List<CommentDto>>

}