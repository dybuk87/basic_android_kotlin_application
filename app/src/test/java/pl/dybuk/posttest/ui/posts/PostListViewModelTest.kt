package pl.dybuk.posttest.ui.posts

import org.junit.Before
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
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncList
import pl.dybuk.health.core.common.list.AsyncListState
import android.arch.core.executor.testing.InstantTaskExecutorRule
import org.fest.assertions.api.Assertions.assertThat
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.dybuk.posttest.RxImmediateSchedulerRule
import pl.dybuk.posttest.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class PostListViewModelTest {

    @Rule
    @JvmField
    val ruleMockito = MockitoJUnit.rule()!!

    @Rule @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var chatRepository : ChatRepository

    @Before
    fun lateinitMocks() {
        chatRepository = Mockito.mock(ChatRepository::class.java)
    }

    @Test
    fun subscribeTest() {
        // When
        val model : PostListViewModel = Mockito.spy(PostListViewModel::class.java)
        val asyncList = AsyncList<Post>()
        Mockito.`when`(chatRepository.getAllPosts()).thenReturn(asyncList);
        val post = Post(Id(1), User(Id(1), "name", "username"), "TITLE", "BODY")
        val state = AsyncListState.update(mutableListOf(post))


        // Given
        model.subscribe(chatRepository)
        asyncList.appendList(post)

        // Then
        Mockito.verify(chatRepository, times(1)).getAllPosts()
        Mockito.verify(model, timeout(1000)).onPostListUpdate(state)
        assertThat(model.postList.value).isEqualsToByComparingFields(state)
        model.unsubscribe()
    }

}