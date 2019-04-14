package pl.dybuk.posttest

import dagger.Component
import pl.dybuk.health.core.CoreModule
import pl.dybuk.posttest.ui.comment.CommentListActivity
import pl.dybuk.posttest.ui.posts.PostListActivity
import pl.dybuk.posttest.ui.welcome.WelcomeActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface AppComponent {
    fun inject(welcomeActivity: WelcomeActivity)
    fun inject(welcomeActivity: PostListActivity)
    fun inject(commentListActivity: CommentListActivity)
    fun inject(coreApplication: CoreApplication)
}