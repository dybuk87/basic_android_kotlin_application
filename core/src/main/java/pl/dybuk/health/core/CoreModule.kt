package pl.dybuk.health.core

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.impl.ChatRepositoryImpl
import pl.dybuk.health.core.service.chat.ChatService
import pl.dybuk.health.core.service.chat.impl.ChatServiceImpl
import pl.dybuk.health.core.service.chat.impl.ChatServiceRetrofit
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
open class CoreModule(private val application : Application) {

    val flavorFactory = FlavorFactoryImpl()

    @Provides
    @Singleton
    open fun providesEventBus() : EventBus = EventBus()

    @Provides
    open fun providesGson() : Gson = GsonBuilder().setLenient().create()

    @Provides
    open fun providesRetrofit(gson:Gson) : Retrofit =
        Retrofit.Builder()
            .baseUrl("http://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    open fun providesCharService(retrofit: Retrofit) = flavorFactory.createChatService(retrofit)

    @Provides
    @Singleton
    open fun providesChatRepository(chatService: ChatService, eventBus: EventBus) : ChatRepository = ChatRepositoryImpl(chatService, eventBus, flavorFactory.randomData())

}