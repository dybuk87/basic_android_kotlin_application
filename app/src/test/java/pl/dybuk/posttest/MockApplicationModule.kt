package pl.dybuk.posttest

import android.app.Application
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.mockito.Mockito
import pl.dybuk.health.core.CoreModule
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.service.chat.ChatService
import retrofit2.Retrofit

class MockApplicationModule(application: Application) : CoreModule(application) {

    override fun providesEventBus(): EventBus {
        return super.providesEventBus()
    }

    override fun providesGson(): Gson {
        return super.providesGson()
    }

    override fun providesRetrofit(gson: Gson): Retrofit {
        return super.providesRetrofit(gson)
    }

    override fun providesCharService(retrofit: Retrofit): ChatService {
        return super.providesCharService(retrofit)
    }

    override fun providesChatRepository(chatService: ChatService, eventBus: EventBus): ChatRepository {
        return Mockito.mock(ChatRepository::class.java)
    }
}