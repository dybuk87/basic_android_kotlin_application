package pl.dybuk.health.core.common.list

data class AsyncListState<T:AsyncListElement>(
    val type : StateType,
    val snapshot : List<T>) {

    enum class StateType {
        NOT_READY,
        INITIALIZED,
        LOADING,
        ERROR
    }

    companion object Factory {
        fun <E:AsyncListElement> create() : AsyncListState<E> = AsyncListState(StateType.NOT_READY, arrayListOf())

        fun <E:AsyncListElement> update(snapshot : List<E>) : AsyncListState<E> = AsyncListState(StateType.INITIALIZED, snapshot)

        fun <E:AsyncListElement> loading(snapshot : List<E>) : AsyncListState<E> = AsyncListState(StateType.LOADING, snapshot)

        fun <E:AsyncListElement> error(snapshot : List<E>) : AsyncListState<E> = AsyncListState(StateType.ERROR, snapshot)

    }

}
