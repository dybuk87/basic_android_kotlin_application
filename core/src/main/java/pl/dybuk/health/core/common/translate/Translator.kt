package pl.dybuk.health.core.common.translate

import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import pl.dybuk.health.core.chat.entity.User

abstract class Translator<A,B> {
    abstract fun forward(source : A):B

    abstract fun backward(source : B):A

    fun forward(source : List<A>) : List<B> =
        Stream.of(source).map { forward(it) }.collect(Collectors.toList()) as List<B>

    fun backward(source : List<B>) : List<A> =
        Stream.of(source).map { backward(it) }.collect(Collectors.toList()) as List<A>

}