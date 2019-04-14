package pl.dybuk.health.core.service.chat.dto

data class AddressDto(
    val street : String,
    val suite : String,
    val city : String,
    val zipcode : String,
    val geo: Geo
)
