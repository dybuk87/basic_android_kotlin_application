package pl.dybuk.health.core

import pl.dybuk.health.core.service.chat.ChatService
import retrofit2.Retrofit

class FlavorFactoryImpl : FlavorFactory {
    override fun randomData() : Boolean = true

    override fun createChatService(retrofit: Retrofit): ChatService = ChatServiceMock()

}