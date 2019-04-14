package pl.dybuk.posttest.ui.comment.adapter

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
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.posttest.RxImmediateSchedulerRule
import pl.dybuk.posttest.TestApplication

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class CommentListAdapterTest {

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
        val commentListAdapter = Mockito.spy(CommentListAdapter())
        commentListAdapter.update(AsyncListState.create())
        assertThat(commentListAdapter.itemCount).isEqualTo(0)

        // given
        commentListAdapter.update(AsyncListState.loading(mutableListOf()))

        // Then
        Mockito.verify(commentListAdapter, times(1)).addLoadingItems()
        assertThat(commentListAdapter.itemCount).isEqualTo(4)
        assertThat(commentListAdapter.getItemViewType(0)).isEqualTo(CommentListAdapter.LOADER_ID)
        assertThat(commentListAdapter.getItemViewType(1)).isEqualTo(CommentListAdapter.LOADER_ID)
        assertThat(commentListAdapter.getItemViewType(2)).isEqualTo(CommentListAdapter.LOADER_ID)
        assertThat(commentListAdapter.getItemViewType(3)).isEqualTo(CommentListAdapter.LOADER_ID)
    }

    @Test
    fun testAddElements() {
        // When
        val commentListAdapter = Mockito.spy(CommentListAdapter())
        commentListAdapter.update(AsyncListState.create())
        assertThat(commentListAdapter.itemCount).isEqualTo(0)
        val comment1 = Mockito.mock(Comment::class.java)
        val comment2 = Mockito.mock(Comment::class.java)
        commentListAdapter.update(AsyncListState.loading(mutableListOf()))

        // Given
        commentListAdapter.update(AsyncListState.update(mutableListOf(comment1, comment2)))

        // Then
        Mockito.verify(commentListAdapter, times(1)).removeLoadingItems()
        Mockito.verify(commentListAdapter, times(1)).setData(mutableListOf(comment1, comment2))
        assertThat(commentListAdapter.itemCount).isEqualTo(2)
        assertThat(commentListAdapter.getItemViewType(0)).isEqualTo(CommentListAdapter.BASIC_ID)
        assertThat(commentListAdapter.getItemViewType(1)).isEqualTo(CommentListAdapter.BASIC_ID)
    }

    @Test
    fun testEmpty() {
        // When
        val commentListAdapter = Mockito.spy(CommentListAdapter())
        commentListAdapter.update(AsyncListState.create())
        assertThat(commentListAdapter.itemCount).isEqualTo(0)
        commentListAdapter.update(AsyncListState.loading(mutableListOf()))

        // Given
        commentListAdapter.update(AsyncListState.update(mutableListOf()))

        // Then
        Mockito.verify(commentListAdapter, times(1)).removeLoadingItems()
        Mockito.verify(commentListAdapter, times(1)).setData(mutableListOf())
        assertThat(commentListAdapter.itemCount).isEqualTo(1)
        assertThat(commentListAdapter.getItemViewType(0)).isEqualTo(CommentListAdapter.EMPTY_ID)
    }

    @Test
    fun testError() {
        // When
        val commentListAdapter = Mockito.spy(CommentListAdapter())
        commentListAdapter.update(AsyncListState.create())
        assertThat(commentListAdapter.itemCount).isEqualTo(0)
        commentListAdapter.update(AsyncListState.loading(mutableListOf()))
        Mockito.verify(commentListAdapter, times(1)).addLoadingItems()
        assertThat(commentListAdapter.itemCount).isEqualTo(4)

        // Given
        commentListAdapter.update(AsyncListState.error(mutableListOf()))

        // Then
        Mockito.verify(commentListAdapter, times(1)).setErrorState()
        assertThat(commentListAdapter.itemCount).isEqualTo(1)
        assertThat(commentListAdapter.getItemViewType(0)).isEqualTo(CommentListAdapter.ERROR_ID)
    }

}