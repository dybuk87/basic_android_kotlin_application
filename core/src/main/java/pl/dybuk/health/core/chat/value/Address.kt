package pl.dybuk.health.core.chat.value

data class Address(
    val street : String,
    val suite : String,
    val city : String,
    val zipCode : String,
    val geoLocation: GeoLocation
)