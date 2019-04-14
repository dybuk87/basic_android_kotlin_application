package pl.dybuk.health.core.common.list

import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.subjects.BehaviorSubject
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.rx.SimpleFuture
import java.util.concurrent.Future

class AsyncList<T : AsyncListElement> {

    private var currentState: AsyncListState<T> = AsyncListState.create()

    var state: BehaviorSubject<AsyncListState<T>> = BehaviorSubject.createDefault(currentState)

    private var list: MutableList<T> = mutableListOf()

    private var emitters : MutableList<Emitter<T>> = mutableListOf()

    @Synchronized
    fun snapshot() : MutableList<T> {
        var snapshot : MutableList<T> = mutableListOf();
        snapshot.addAll(list)
        return snapshot
    }

    @Synchronized
    fun loading() {
        updateState(AsyncListState.loading(currentState.snapshot))
    }

    @Synchronized
    fun appendList(item : T) {
        list.add(item)
        updateState(AsyncListState.update(list.toMutableList()))

        emitters.removeAll(Stream.of(emitters).filter { emitter -> emitter.accept(item) }.collect(Collectors.toList()))

    }

    @Synchronized
    fun appendList(items: List<T>) {
        list.addAll(items)

        updateState(AsyncListState.update(list.toMutableList()))

        Stream.of(items).forEach{item ->
            emitters.removeAll(Stream.of(emitters).filter { emitter -> emitter.accept(item) }.collect(Collectors.toList()))
        }
    }

    @Synchronized
    fun error() {
        updateState(AsyncListState.error(currentState.snapshot))
    }

    @Synchronized
    private fun updateState(newState: AsyncListState<T>) {
        currentState = newState
        state.onNext(currentState)
    }

    @Synchronized
    fun findById(id : Id) : T? {
        return Stream.of(list).filter { it.id == id }.findFirst().orElse(null)
    }

    @Synchronized
    fun reset() {
        list.clear()
        updateState(AsyncListState.create())
    }

    @Synchronized
    fun whenReady(id: Id): Observable<T> {
        val item : T? = findById(id)
        if (item != null) {
            return Observable.just(item)
        } else {
            val simpleFuture = SimpleFuture<T>()
            emitters.add(Emitter(id, simpleFuture))
            return Observable.fromFuture(simpleFuture)
        }
    }

    internal class Emitter<T: AsyncListElement> (val id : Id, private val future : SimpleFuture<T>) {

        fun accept(value : T) : Boolean {
            if (value.id == id) {
                future.setResult(value)
                return true
            }
            return false
        }
    }

}