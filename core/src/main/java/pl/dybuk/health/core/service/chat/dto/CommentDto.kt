package pl.dybuk.health.core.service.chat.dto

data class CommentDto(
    val postId : Int,
    val id : Int,
    val name : String,
    val email: String,
    val body : String
)