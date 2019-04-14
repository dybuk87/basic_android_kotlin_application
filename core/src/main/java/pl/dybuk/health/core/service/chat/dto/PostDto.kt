package pl.dybuk.health.core.service.chat.dto

data class PostDto(
    val userId : Int,
    val  id : Int,
    val title : String,
    val body: String
)