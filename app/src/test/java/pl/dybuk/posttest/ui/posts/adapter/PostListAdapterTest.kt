package pl.dybuk.posttest.ui.posts.adapter

import android.arch.core.executor.testing.InstantTaskExecutorRule
import org.fest.assertions.api.Assertions.assertThat
import org.greenrobot.eventbus.EventBus
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.posttest.RxImmediateSchedulerRule
import pl.dybuk.posttest.TestApplication


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class PostListAdapterTest {

    @Rule
    @JvmField
    val ruleMockito = MockitoJUnit.rule()!!

    @Rule
    @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var eventbus : EventBus

    lateinit var chatRepository : ChatRepository

    @Before
    fun init() {
        // EventBus.getDefault().register(this)
    }

    @Test
    fun testLoading() {
        // When
        val postListAdapter = Mockito.spy(PostListAdapter(eventbus))
        postListAdapter.update(AsyncListState.create())
        assertThat(postListAdapter.itemCount).isEqualTo(0)

        // Given
        postListAdapter.update(AsyncListState.loading(mutableListOf()))

        // Then
        Mockito.verify(postListAdapter, times(1)).addLoadingItems()
        assertThat(postListAdapter.itemCount).isEqualTo(4)
        assertThat(postListAdapter.getItemViewType(0)).isEqualTo(PostListAdapter.LOADER_ID)
        assertThat(postListAdapter.getItemViewType(1)).isEqualTo(PostListAdapter.LOADER_ID)
        assertThat(postListAdapter.getItemViewType(2)).isEqualTo(PostListAdapter.LOADER_ID)
        assertThat(postListAdapter.getItemViewType(3)).isEqualTo(PostListAdapter.LOADER_ID)
    }

    @Test
    fun testAddElements() {
        // When
        val postListAdapter = Mockito.spy(PostListAdapter(eventbus))
        postListAdapter.update(AsyncListState.create())
        assertThat(postListAdapter.itemCount).isEqualTo(0)
        val post1 = Mockito.mock(Post::class.java)
        val post2 = Mockito.mock(Post::class.java)
        postListAdapter.update(AsyncListState.loading(mutableListOf()))
        Mockito.verify(postListAdapter, times(1)).addLoadingItems()
        assertThat(postListAdapter.itemCount).isEqualTo(4)

        // Given
        postListAdapter.update(AsyncListState.update(mutableListOf(post1, post2)))

        // Then
        Mockito.verify(postListAdapter, times(1)).removeLoadingItems()
        Mockito.verify(postListAdapter, times(1)).setData(mutableListOf(post1, post2))
        assertThat(postListAdapter.itemCount).isEqualTo(2)
        assertThat(postListAdapter.getItemViewType(0)).isEqualTo(PostListAdapter.BASIC_ID)
        assertThat(postListAdapter.getItemViewType(1)).isEqualTo(PostListAdapter.BASIC_ID)
    }

    @Test
    fun testEmpty() {
        // When
        val postListAdapter = Mockito.spy(PostListAdapter(eventbus))
        postListAdapter.update(AsyncListState.create())
        assertThat(postListAdapter.itemCount).isEqualTo(0)
        postListAdapter.update(AsyncListState.loading(mutableListOf()))
        Mockito.verify(postListAdapter, times(1)).addLoadingItems()
        assertThat(postListAdapter.itemCount).isEqualTo(4)

        // Given
        postListAdapter.update(AsyncListState.update(mutableListOf()))

        // Then
        Mockito.verify(postListAdapter, times(1)).removeLoadingItems()
        Mockito.verify(postListAdapter, times(1)).setData(mutableListOf())
        assertThat(postListAdapter.itemCount).isEqualTo(1)

        assertThat(postListAdapter.getItemViewType(0)).isEqualTo(PostListAdapter.EMPTY_ID)
    }

    @Test
    fun testError() {
        // When
        val postListAdapter = Mockito.spy(PostListAdapter(eventbus))
        postListAdapter.update(AsyncListState.create())
        assertThat(postListAdapter.itemCount).isEqualTo(0)
        postListAdapter.update(AsyncListState.loading(mutableListOf()))
        Mockito.verify(postListAdapter, times(1)).addLoadingItems()
        assertThat(postListAdapter.itemCount).isEqualTo(4)

        // Given
        postListAdapter.update(AsyncListState.error(mutableListOf()))

        // Then
        Mockito.verify(postListAdapter, times(1)).setErrorState()
        assertThat(postListAdapter.itemCount).isEqualTo(1)
        assertThat(postListAdapter.getItemViewType(0)).isEqualTo(PostListAdapter.ERROR_ID)
    }

}