package pl.dybuk.health.core.chat.impl

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.observers.TestObserver
import org.greenrobot.eventbus.EventBus
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.timeout
import org.mockito.Mockito.times
import org.mockito.runners.MockitoJUnitRunner
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.entity.User
import pl.dybuk.health.core.chat.event.LoadCommentsEvent
import pl.dybuk.health.core.chat.translate.PostTranslator
import pl.dybuk.health.core.chat.translate.UserTranslator
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.health.core.service.chat.ChatService
import pl.dybuk.health.core.service.chat.dto.*


@RunWith(MockitoJUnitRunner::class)
class ChatRepositoryImplTest {

    @Mock
    lateinit var chatService : ChatService

    @Mock
    lateinit var eventBus : EventBus

    @Test
    fun test1() {
        // When
        val repo = ChatRepositoryImpl(chatService, eventBus, false)
        val userObservable : Observable<List<UserDto>> = Observable.just(mutableListOf())
        Mockito.`when`(chatService.getUsers()).thenReturn(userObservable)
        val postObservable : Observable<List<PostDto>> = Observable.just(mutableListOf())
        Mockito.`when`(chatService.getPosts()).thenReturn(postObservable)

        // Given
        val posts = repo.getAllPosts()

        // Then
        Mockito.verify(chatService, timeout(2000)).getUsers()
        Mockito.verify(chatService, timeout(2000)).getPosts()

    }


    @Test
    fun test2() {
        // When
        val repo = ChatRepositoryImpl(chatService, eventBus, false)
        val userDto = UserDto(1, "NAME", "USERNAME",
            AddressDto("Street", "suite", "City", "zip-code", Geo("1", "2")),
            "111", "web",
            CompanyDto("name", "phase", "bs")
        )
        val postDto = PostDto(1, 2, "TITLE", "BODY")
        val userObservable : Observable<List<UserDto>> = Observable.just(mutableListOf(userDto))
        Mockito.`when`(chatService.getUsers()).thenReturn(userObservable)
        val postObservable : Observable<List<PostDto>> = Observable.just(mutableListOf(postDto))
        Mockito.`when`(chatService.getPosts()).thenReturn(postObservable)

        // Given
        val post = repo.findPostById(Id(2))
        val subscriber = TestObserver<Post>()
        post.subscribe(subscriber)

        // Then
        subscriber.await()
        subscriber.assertComplete()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)

        val userTranslator = UserTranslator()
        val user = userTranslator.forward(userDto)

        subscriber.assertValues(
            PostTranslator(
                mutableListOf(user)
            ).forward(postDto)
        )

        Mockito.verify(chatService, times(1)).getUsers()
        Mockito.verify(chatService, times(1)).getPosts()

    }

    @Test
    fun test3() {
        // When
        val repo = ChatRepositoryImpl(chatService, eventBus, false)
        val userDto = UserDto(1, "NAME", "USERNAME",
            AddressDto("Street", "suite", "City", "zip-code", Geo("1", "2")),
            "111", "web",
            CompanyDto("name", "phase", "bs")
        )
        val postDto = PostDto(1, 2, "TITLE", "BODY")
        val commentDto = CommentDto(2, 1, "NAME", "EMAIL", "BODY")
        val userTranslator = UserTranslator()
        val user = userTranslator.forward(userDto)
        val post = PostTranslator(
            mutableListOf(user)
        ).forward(postDto)
        val commentObservable : Observable<List<CommentDto>> = Observable.just(mutableListOf(commentDto))
        Mockito.`when`(chatService.getComments()).thenReturn(commentObservable)

        // Given
        repo.loadComments(LoadCommentsEvent(post))

        // Then
        Mockito.verify(chatService, timeout(2000)).getComments()


    }
}