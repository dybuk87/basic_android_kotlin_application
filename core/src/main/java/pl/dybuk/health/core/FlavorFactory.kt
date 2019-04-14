package pl.dybuk.health.core

import pl.dybuk.health.core.service.chat.ChatService
import retrofit2.Retrofit

interface FlavorFactory {
    fun randomData() : Boolean

    fun createChatService(retrofit: Retrofit) : ChatService

}