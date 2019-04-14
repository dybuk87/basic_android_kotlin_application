package pl.dybuk.posttest.ui.welcome

import android.app.Activity
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Intent
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.timeout
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnit
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import pl.dybuk.posttest.ActivityWrap
import pl.dybuk.posttest.RxImmediateSchedulerRule
import pl.dybuk.posttest.TestApplication
import pl.dybuk.posttest.ui.comment.CommentListActivity
import pl.dybuk.posttest.ui.posts.PostListActivity

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class WelcomeActivityTest {

    @Rule
    @JvmField
    val ruleMockito = MockitoJUnit.rule()!!

    @Rule
    @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @Test
    fun testOpenPostListActivity() {
        // When
        val controller = Robolectric.buildActivity(WelcomeActivity::class.java!!)
        val activity = ActivityWrap.getSpy(controller)
        val intent = ArgumentCaptor.forClass(Intent::class.java)

        // Given
        controller.create().resume()

        // Then
        shadowOf(activity.handler.getLooper()).getScheduler().advanceToNextPostedRunnable()
        Mockito.verify(activity, times(1)).startActivity(intent.capture())

        assertThat(intent.value.component.className).isEqualTo(PostListActivity::class.java.canonicalName)

    }

}