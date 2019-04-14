package pl.dybuk.health.core.service.chat.impl

import io.reactivex.Observable
import pl.dybuk.health.core.service.chat.ChatService
import pl.dybuk.health.core.service.chat.dto.CommentDto
import pl.dybuk.health.core.service.chat.dto.PostDto
import pl.dybuk.health.core.service.chat.dto.UserDto

class ChatServiceImpl(private val chatServiceRetrofit: ChatServiceRetrofit) : ChatService {

    override fun getUsers(): Observable<List<UserDto>> = chatServiceRetrofit.getUsers()

    override fun getPosts(): Observable<List<PostDto>> = chatServiceRetrofit.getPosts()

    override fun getComments(): Observable<List<CommentDto>> = chatServiceRetrofit.getComments()

}