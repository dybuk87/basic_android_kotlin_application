package pl.dybuk.health.core

import pl.dybuk.health.core.service.chat.ChatService
import pl.dybuk.health.core.service.chat.impl.ChatServiceImpl
import pl.dybuk.health.core.service.chat.impl.ChatServiceRetrofit
import retrofit2.Retrofit


class FlavorFactoryImpl : FlavorFactory {

    override fun randomData() : Boolean = false

    override fun createChatService(retrofit: Retrofit): ChatService =
        ChatServiceImpl(ChatServiceRetrofit.create(retrofit))

}