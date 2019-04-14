package pl.dybuk.health.core.common.rx

import android.util.Log
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.lang.RuntimeException

class CombineFlatMap {

    internal class Task<T>(val observable: Observable<T>, val consumer : Consumer<T>)

    private val tasks : MutableList<Task<*>> = mutableListOf()

    private var onError: (th:Throwable) -> Unit = {
        it.printStackTrace()
    }

    fun <T> next(observable: Observable<T>, consumer : Consumer<T>): CombineFlatMap {
        tasks.add(Task(observable, consumer))
        return this
    }

    fun onError(function: (th:Throwable) -> Unit): CombineFlatMap {
        this.onError = function
        return this
    }

    fun start() {
        val combined = build<Any>()
        combined?.subscribe(
            tasks[tasks.size-1].consumer as Consumer<in Any>?,
            Consumer{ onError(it) }
        )
    }

    fun <T> build() : Observable<T>? {
        var result : Observable<T>? = null
        if (tasks.size == 1) {
            val task0 : Task<Any> = tasks[0] as Task<Any>
            result = task0.observable
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()) as Observable<T>
        } else if (tasks.size > 1) {
            val task0 : Task<Any> = tasks[0] as Task<Any>
            var taskI : Task<Any> = tasks[1] as Task<Any>

            var combined = task0.observable
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .flatMap {
                    task0.consumer.accept(it)
                    taskI.observable
                }
            for(i in 1 .. tasks.size - 2) {
                combined = combined
                   .flatMap {
                       val taskI = tasks[i] as Task<Any>
                       val taskJ = tasks[i + 1] as Task<Any>
                       taskI.consumer.accept(it)
                       taskJ.observable
                   }
            }

            result = combined as Observable<T>
        }

        return result
    }


}