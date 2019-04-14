package pl.dybuk.health.core.chat

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncList

interface ChatRepository  {

    fun getAllPosts() : AsyncList<Post>

    fun reload()

    fun findPostById(id : Id) : Observable<Post>

}