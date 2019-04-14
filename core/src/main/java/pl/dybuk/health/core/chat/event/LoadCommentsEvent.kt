package pl.dybuk.health.core.chat.event

import pl.dybuk.health.core.chat.entity.Post

data class LoadCommentsEvent(val post:Post)