package pl.dybuk.posttest

import android.app.Application
import pl.dybuk.health.core.CoreModule
import pl.dybuk.health.core.chat.ChatRepository
import javax.inject.Inject

open class CoreApplication : Application() {

    open lateinit var appComponent: AppComponent

    @Inject
    lateinit var chatRepository : ChatRepository

    override fun onCreate() {
        super.onCreate()

        init()
    }


    open fun init() {
        appComponent = DaggerAppComponent
            .builder().coreModule(CoreModule(this))
            .build()

        appComponent.inject(this)
    }

}
