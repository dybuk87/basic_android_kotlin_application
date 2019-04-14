package pl.dybuk.health.core.common.list

import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import pl.dybuk.health.core.chat.value.Id
import java.util.concurrent.TimeUnit
import pl.dybuk.health.core.Dummy


@RunWith(MockitoJUnitRunner::class)
class AsyncListTest {

    @Test
    fun testInitState() {
        // When
        val list = AsyncList<Dummy>()
        val subscriber = TestObserver<AsyncListState<Dummy>>()

        // Given
        list.state.subscribe(subscriber)

        // Then
        subscriber.assertNotComplete()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValues(AsyncListState.create())
        assertThat(list.snapshot()).isEmpty()
    }

    @Test
    fun testSuccessLoading() {
        // When
        val list = AsyncList<Dummy>()
        val subscriber = TestObserver<AsyncListState<Dummy>>()

        // Given
        list.state.subscribe(subscriber)
        list.loading()
        list.appendList(Dummy(Id(1)))

        // Then
        subscriber.assertNotComplete()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(3)
        subscriber.assertValues(
            AsyncListState.create(),
            AsyncListState.loading(mutableListOf()),
            AsyncListState.update(mutableListOf(Dummy(Id(1))))
        )
        assertThat(list.snapshot()).isEqualTo(mutableListOf(Dummy(Id(1))))

    }

    @Test
    fun testSuccessLoadingMore() {
        // When
        val list = AsyncList<Dummy>()
        val subscriber = TestObserver<AsyncListState<Dummy>>()
        list.loading()
        list.appendList(Dummy(Id(1)))

        // Given
        list.state.subscribe(subscriber)
        list.loading()
        list.appendList(mutableListOf(Dummy(Id(2)), Dummy(Id(3))))

        // Then
        subscriber.assertNotComplete()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(3)

        subscriber.assertValues(
            AsyncListState.update(mutableListOf(Dummy(Id(1)))),
            AsyncListState.loading(mutableListOf(Dummy(Id(1)))),
            AsyncListState.update(mutableListOf(Dummy(Id(1)), Dummy(Id(2)), Dummy(Id(3))))
        )

        assertThat(list.snapshot())
            .isEqualTo(mutableListOf(Dummy(Id(1)), Dummy(Id(2)), Dummy(Id(3))))

    }

    @Test
    fun testErrorLoading() {
        // When
        val list = AsyncList<Dummy>()
        val subscriber = TestObserver<AsyncListState<Dummy>>()

        // Given
        list.state.subscribe(subscriber)

        list.loading()
        list.appendList(Dummy(Id(1)))
        list.loading()
        list.error()

        // Then
        subscriber.assertNotComplete()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(5)

        subscriber.assertValues(
            AsyncListState.create(),
            AsyncListState.loading(mutableListOf()),
            AsyncListState.update(mutableListOf(Dummy(Id(1)))),
            AsyncListState.loading(mutableListOf(Dummy(Id(1)))),
            AsyncListState.error(mutableListOf(Dummy(Id(1))))
        )

        assertThat(list.snapshot())
            .isEqualTo(mutableListOf(Dummy(Id(1))))

    }

    @Test
    fun findByTest() {
        // When
        val list = AsyncList<Dummy>()

        // Given
        list.appendList(
            mutableListOf(Dummy(Id(2)), Dummy(Id(3)))
        )

        // Then
        assertThat(list.findById(Id(2))).isEqualTo(Dummy(Id(2)))
        assertThat(list.findById(Id(3))).isEqualTo(Dummy(Id(3)))
        assertThat(list.findById(Id(4))).isNull()
    }

    @Test
    fun whenReadyTest() {
        val list = AsyncList<Dummy>()

        var testObserver = TestObserver<Dummy>()
        list.whenReady(Id(2))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(testObserver)


        testObserver.assertNotComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(0)
        list.appendList(Dummy(Id(1)))

        testObserver.await(300, TimeUnit.MILLISECONDS)

        testObserver.assertNotComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(0)

        list.appendList(Dummy(Id(2)))

        testObserver.await()

        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue(Dummy(Id(2)))

        testObserver = TestObserver<Dummy>()

        list.whenReady(Id(1)).subscribe(testObserver)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue(Dummy(Id(1)))
    }

    @Test
    fun testListReset() {

        val list = AsyncList<Dummy>()

        var testObserver = TestObserver<AsyncListState<Dummy>>()

        list.state.subscribe(testObserver)

        testObserver.assertNotComplete()
        testObserver.assertValueCount(1)
        testObserver.assertValue(AsyncListState.create())

        list.appendList(Dummy(Id(2)))

        testObserver.assertNotComplete()
        testObserver.assertValueCount(2)
        testObserver.assertValues(
            AsyncListState.create(),
            AsyncListState.update(mutableListOf(Dummy(Id(2)))))

        list.reset()

        testObserver.assertNotComplete()
        testObserver.assertValueCount(3)
        testObserver.assertValues(
            AsyncListState.create(),
            AsyncListState.update(mutableListOf(Dummy(Id(2)))),
            AsyncListState.create())
    }

}