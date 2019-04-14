package pl.dybuk.health.core.chat.entity

import org.greenrobot.eventbus.EventBus
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncList
import pl.dybuk.health.core.common.list.AsyncListElement

class Post (
    override val id : Id,
    val user : User,
    var title : String,
    var body : String) : AsyncListElement {

    val comments : AsyncList<Comment> = AsyncList()

    fun changeTitle(title :String) {
        this.title = title
    }

    fun changeDescription(body : String) {
        this.body = body
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}