package pl.dybuk.posttest.ui.comment

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Intent
import android.os.Bundle

import android.util.Log
import io.reactivex.Observable
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.timeout
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnit
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.entity.User
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncList
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.posttest.ActivityWrap
import pl.dybuk.posttest.RxImmediateSchedulerRule
import pl.dybuk.posttest.TestApplication
import pl.dybuk.posttest.ActivityWrap.getSpy




@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class CommentListActivityTest {

    @Rule
    @JvmField
    val ruleMockito = MockitoJUnit.rule()!!

    @Rule
    @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    val post = Post(Id(1), User(Id(1), "name", "username"), "TITLE", "BODY")
    val comment = Comment(Id(1), "NAME", null, "Body")

    @Test
    fun testBindToCommentWhenActivityCreated() {
        // When
        val intent = Intent()
        intent.putExtra(CommentListActivity.POST_ID, Id(1))
        val application = (RuntimeEnvironment.application as TestApplication)
        Mockito.`when`(application.chatRepository.findPostById(Id(1))).thenReturn(Observable.just(post))
        val controller = Robolectric.buildActivity(CommentListActivity::class.java!!, intent)
        val activity = ActivityWrap.getSpy(controller)

        // Given
        controller.create().resume()

        // Then
        post.comments.appendList(comment)
        Mockito.verify(activity, timeout(3000)).postListUpdate(AsyncListState.update(mutableListOf(comment)))
        Mockito.verify(activity, times(1)).postListUpdate(AsyncListState.update(mutableListOf(comment)));

    }
}