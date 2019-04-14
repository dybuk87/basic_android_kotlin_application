package pl.dybuk.health.core.common.rx

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.dybuk.health.core.Dummy
import pl.dybuk.health.core.chat.value.Id
import org.fest.assertions.api.Assertions.assertThat
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RunWith(MockitoJUnitRunner::class)
class SimpleFutureTest {

    @Test(expected = TimeoutException::class)
    fun testTimeout() {
        val future = SimpleFuture<Dummy>()
        future.get(100, TimeUnit.MILLISECONDS)
    }

    @Test()
    fun testValueAlreadyPresentGetWithTime() {
        // When
        val future = SimpleFuture<Dummy>()

        // Given
        future.setResult(Dummy(Id(1)))

        // Then
        val result = future.get(100, TimeUnit.MILLISECONDS)
        assertThat(result).isEqualTo(Dummy(Id(1)))
    }

    @Test()
    fun testValueAlreadyPresentSimpleGet() {
        // When
        val future = SimpleFuture<Dummy>()

        // Given
        future.setResult(Dummy(Id(1)))

        // Then
        assertThat(future.get()).isEqualTo(Dummy(Id(1)));
    }


    @Test()
    fun testValuePushGetWithTime() {
        // When
        val future = SimpleFuture<Dummy>()
        Thread { Thread.sleep(300);  future.setResult(Dummy(Id(1))) }.start()

        // Given
        val result = future.get(5000, TimeUnit.MILLISECONDS)

        // Then
        assertThat(result).isEqualTo(Dummy(Id(1)))
    }

    @Test()
    fun testValuePushSimpleGet() {
        // When
        val future = SimpleFuture<Dummy>()
        Thread { Thread.sleep(300); future.setResult(Dummy(Id(1))) }.start()

        // Given
        val result = future.get()

        // Then
        assertThat(result).isEqualTo(Dummy(Id(1)))
    }



    @Test(expected = ExecutionException::class)
    fun testErrorAlreadyPresentGetWithTime() {
        // When
        val future = SimpleFuture<Dummy>()

        // Given
        future.setError(Throwable("ERROR"))

        // Then
        future.get(100, TimeUnit.MILLISECONDS)
    }

    @Test(expected = ExecutionException::class)
    fun testErrorAlreadyPresentSimpleGet() {
        // When
        val future = SimpleFuture<Dummy>()

        // Given
        future.setError(Throwable("ERROR"))

        // Then
        future.get()
    }


    @Test(expected = ExecutionException::class)
    fun testErrorPushGetWithTime() {
        // When
        val future = SimpleFuture<Dummy>()
        Thread { Thread.sleep(300); future.setError(Throwable("ERROR")) }.start()

        // Given
        val result = future.get(5000, TimeUnit.MILLISECONDS)

        // Then
        assertThat(result).isEqualTo(Dummy(Id(1)))
    }

    @Test(expected = ExecutionException::class)
    fun testErrorPushSimpleGet() {
        // When
        val future = SimpleFuture<Dummy>()
        Thread { Thread.sleep(300); future.setError(Throwable("ERROR")) }.start()

        // Given
        val result = future.get()

        // Then
        assertThat(result).isEqualTo(Dummy(Id(1)))
    }

    @Test
    fun testStateChangeToCancel() {
        // When
        val future = SimpleFuture<Dummy>()
        assertThat(future.isCancelled).isFalse
        assertThat(future.isDone).isFalse

        // Given
        future.cancel(true)

        // Then
        assertThat(future.isCancelled).isTrue
        assertThat(future.isDone).isFalse
    }

    @Test
    fun testStateChangeOnResultSet() {
        // When
        val future = SimpleFuture<Dummy>()
        assertThat(future.isCancelled).isFalse
        assertThat(future.isDone).isFalse

        // Given
        future.setResult(Dummy(Id(1)))

        // Then
        assertThat(future.isCancelled).isFalse
        assertThat(future.isDone).isTrue
    }

    @Test
    fun testStateChangeOnErrorSet() {
        // When
        val future = SimpleFuture<Dummy>()
        assertThat(future.isCancelled).isFalse
        assertThat(future.isDone).isFalse

        // Given
        future.setError(Throwable("ERROR"))

        // Then
        assertThat(future.isCancelled).isFalse
        assertThat(future.isDone).isTrue
    }

}