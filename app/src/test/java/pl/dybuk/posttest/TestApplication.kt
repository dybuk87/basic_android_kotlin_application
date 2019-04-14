package pl.dybuk.posttest

class TestApplication : CoreApplication() {

    override lateinit var appComponent: AppComponent


    override fun init() {
        appComponent = DaggerAppComponent
            .builder()
                .coreModule(MockApplicationModule(this))
            .build()

        appComponent.inject(this)
    }
}