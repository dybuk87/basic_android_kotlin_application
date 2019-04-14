package pl.dybuk.health.core.chat.entity

import pl.dybuk.health.core.chat.value.*
import pl.dybuk.health.core.common.list.AsyncListElement

class User(
    override val id : Id,
    val name : String,
    val username : String,
    var address: Address? = null,
    var phone : Phone? = null,
    var website: Website? = null,
    var company : Company? = null)  : AsyncListElement {

    fun moveTo(address: Address) {
        this.address = address
    }

    fun changePhoneNumber(phone: Phone) {
        this.phone = phone
    }

    fun changeWebsite(website: Website?) {
        this.website = website
    }

    fun changeEmployeer(company: Company?) {
        this.company = company
    }

}