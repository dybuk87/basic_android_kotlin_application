package pl.dybuk.health.core.chat.entity

import pl.dybuk.health.core.chat.value.Email
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncListElement

class Comment  (
    override val id: Id,
    val name : String,
    var email : Email?,
    var body : String) : AsyncListElement {


    fun modifyComment(body : String) {
        this.body = body
    }

    fun changeEmail(email: Email) {
        this.email = email;
    }

}