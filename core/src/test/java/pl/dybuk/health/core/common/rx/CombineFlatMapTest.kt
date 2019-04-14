package pl.dybuk.health.core.common.rx

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.functions.Consumer
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import org.fest.assertions.api.Assertions.assertThat
import org.mockito.Mockito
import org.mockito.Mockito.timeout
import org.mockito.Mockito.times
import pl.dybuk.health.core.Dummy
import pl.dybuk.health.core.chat.value.Id
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

@RunWith(MockitoJUnitRunner::class)
class CombineFlatMapTest {

    @Test
    fun combineEmpty() {
        val combine = CombineFlatMap()
        assertThat(combine.build<Any>()).isNull()
        combine.start()
    }

    @Test
    fun combineOne() {
        // When
        val combine = CombineFlatMap()
        val observable = Observable.just(Dummy(Id(1)))
        val consumer = Mockito.mock(Consumer::class.java) as Consumer<Dummy>

        // Given
        combine.next(observable, consumer)
        combine.start()

        // Then
        Mockito.verify(consumer, timeout(1000)).accept(Dummy(Id(1)))
        Mockito.verify(consumer, times(1))
            .accept(Dummy(Id(1)))
    }

    @Test
    fun combineTwo() {
        // When
        val combine = CombineFlatMap()
        val observable1 = Observable.just(Dummy(Id(1)))
        val observable2 = Observable.just(Dummy(Id(2)))
        val consumer = Mockito.mock(Consumer::class.java) as Consumer<Dummy>

        // Given
        combine.next(observable1, consumer)
        combine.next(observable2, consumer)
        combine.start()

        // Then
        Mockito.verify(consumer, timeout(1000)).accept(Dummy(Id(2)))
        Mockito.verify(consumer, times(1))
            .accept(Dummy(Id(1)))
        Mockito.verify(consumer, times(1))
            .accept(Dummy(Id(2)))
    }

    @Test
    fun combineThree() {
        // When
        val combine = CombineFlatMap()
        val observable1 = Observable.just(Dummy(Id(1)))
        val observable2 = Observable.just(Dummy(Id(2)))
        val observable3 = Observable.just(Dummy(Id(3)))
        val consumer = Mockito.mock(Consumer::class.java) as Consumer<Dummy>

        // Given
        combine.next(observable1, consumer)
        combine.next(observable2, consumer)
        combine.next(observable3, consumer)
        combine.start()

        // Then
        Mockito.verify(consumer, timeout(1000)).accept(Dummy(Id(3)))
        Mockito.verify(consumer, times(1))
            .accept(Dummy(Id(1)))
        Mockito.verify(consumer, times(1))
            .accept(Dummy(Id(2)))
        Mockito.verify(consumer, times(1))
            .accept(Dummy(Id(3)))
    }


    @Test
    fun combineBreakChain() {
        // When
        val combine = CombineFlatMap()
        val observable1 = Observable.just(Dummy(Id(1)))
        val observable2 = Observable.create<Dummy> { throw RuntimeException("EXCEPTION") }
        val observable3 = Observable.just(Dummy(Id(3)))
        val consumer = Mockito.mock(Consumer::class.java) as Consumer<Dummy>
        val lock = SimpleFuture<Boolean>()

        // Given
        combine.next(observable1, consumer)
        combine.next(observable2, consumer)
        combine.next(observable3, consumer)
        combine.onError { lock.setResult(true) }
        combine.start()


        // Then
        assertThat(lock.get(1, TimeUnit.SECONDS)).isTrue
        Mockito.verify(consumer, times(1))
            .accept(Dummy(Id(1)))
        Mockito.verify(consumer, times(0))
            .accept(Dummy(Id(2)))
        Mockito.verify(consumer, times(0))
            .accept(Dummy(Id(3)))


    }

}