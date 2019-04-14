package pl.dybuk.posttest.ui.comment

import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.timeout
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner

import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.entity.User
import pl.dybuk.health.core.chat.value.Address
import pl.dybuk.health.core.chat.value.GeoLocation
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncList
import pl.dybuk.health.core.common.list.AsyncListState
import android.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.Observable
import org.fest.assertions.api.Assertions.assertThat
import org.greenrobot.eventbus.EventBus
import org.junit.rules.TestRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.posttest.RxImmediateSchedulerRule
import pl.dybuk.posttest.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class CommentListViewModelTest {

    @Rule
    @JvmField
    val ruleMockito = MockitoJUnit.rule()!!

    @Rule @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var chatRepository : ChatRepository

    lateinit var eventBus : EventBus

    @Before
    fun lateinitMocks() {
        chatRepository = Mockito.mock(ChatRepository::class.java)
        eventBus = Mockito.mock(EventBus::class.java)
    }

    @Test
    fun subscribeToExistingPost() {
        // When
        val post = Post(Id(1), User(Id(1), "name", "username"), "TITLE", "BODY")
        val comment = Comment(Id(1), "NAME", null, "Body")
        val model : CommentListViewModel = Mockito.spy(CommentListViewModel::class.java)
        val asyncList = AsyncList<Post>()
        Mockito.`when`(chatRepository.findPostById(Id(1))).thenReturn(Observable.just(post))
        val state = AsyncListState.update(mutableListOf(comment))

        // Given
        model.subscribe(eventBus, chatRepository, Id(1))
        asyncList.appendList(post)

        // Then
        post.comments.appendList(comment)
        Mockito.verify(chatRepository, times(1)).findPostById(Id(1))
        Mockito.verify(model, timeout(1000)).onCommentListUpdate(state)
        assertThat(model.commentList.value).isEqualsToByComparingFields(state)
        model.unsubscribe()
    }

}