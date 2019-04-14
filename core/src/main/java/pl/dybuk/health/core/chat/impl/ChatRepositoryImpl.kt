package pl.dybuk.health.core.chat.impl

import android.util.Log
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.entity.User
import pl.dybuk.health.core.chat.event.LoadCommentsEvent
import pl.dybuk.health.core.chat.translate.CommentTranslator
import pl.dybuk.health.core.chat.translate.PostTranslator
import pl.dybuk.health.core.chat.translate.UserTranslator
import pl.dybuk.health.core.chat.value.Email
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncList
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.health.core.common.rx.CombineFlatMap
import pl.dybuk.health.core.service.chat.ChatService
import pl.dybuk.health.core.service.chat.dto.CommentDto
import pl.dybuk.health.core.service.chat.dto.PostDto
import pl.dybuk.health.core.service.chat.impl.ChatServiceRetrofit
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

class ChatRepositoryImpl(val chatService: ChatService, val eventBus : EventBus, val randomData : Boolean) : ChatRepository {

    init {
        eventBus.register(this)
    }

    override fun findPostById(id: Id): Observable<Post> {
        var result =  posts.whenReady(id).subscribeOn(Schedulers.io())
        if (!postLoaded()) {
            result = loadAllData()
                .next(result, Consumer{
                    Log.i("CHAT", "CONSUME")
                })
                .build()
        }
        return result
    }


    val users: AsyncList<User> = AsyncList()

    val posts: AsyncList<Post> = AsyncList()

    val comments : AtomicBoolean = AtomicBoolean(false)

    private fun usersLoaded() = users.state.value?.type != AsyncListState.StateType.NOT_READY &&
            users.state.value?.type != AsyncListState.StateType.ERROR

    private fun postLoaded() = posts.state.value?.type != AsyncListState.StateType.NOT_READY &&
            posts.state.value?.type != AsyncListState.StateType.ERROR


    private fun loadUserObservable(combined:CombineFlatMap) : CombineFlatMap {
        if (!usersLoaded()) {
            users.loading()
            combined.next(chatService.getUsers(), Consumer {
                val userTranslator = UserTranslator()
                users.appendList(
                    userTranslator.forward(it)
                )
            })
        }
        return combined
    }

    private fun loadPostsObservable(combined:CombineFlatMap) : CombineFlatMap {
        if (!postLoaded()) {
            posts.loading()
            combined.next(
                chatService.getPosts(), Consumer {
                    val postTranslator = PostTranslator(users.snapshot())
                    val list = postTranslator.forward(it)
                    posts.appendList(list)
                    Stream.of(list)
                        .forEach { it.comments.loading() }

                    if (randomData) {
                        generateRandomData()
                    }
                })

        }
        return combined
    }

    private fun loadCommentsObservable(combined:CombineFlatMap) : CombineFlatMap {
        combined.next(
            chatService.getComments(), Consumer {
                val commentTranslator = CommentTranslator()

                Stream.of(it)
                    .groupBy(CommentDto::postId)
                    .filter { item -> item.value.size > 0 }
                    .forEach { entry ->
                        val post = posts.findById(Id(entry.value[0].postId))

                        post?.comments?.appendList(
                            commentTranslator.forward(entry.value)
                        )
                    }


            }
        )
        return combined
    }

    private fun loadAllData() : CombineFlatMap {

        val combined = CombineFlatMap()

        loadUserObservable(combined)
        loadPostsObservable(combined)

        combined.onError {
            it.printStackTrace()
            users.error()
            posts.error()
        }


        return combined
    }


    override fun getAllPosts(): AsyncList<Post> {
        if (!usersLoaded() || !postLoaded()) {
            reload()
        }
        return posts
    }


    private fun generateRandomData() {
        Thread {
            for (i in 0..500) {

                val incoming = posts.snapshot()

                val index = i + 1000
                incoming[Random.nextInt(incoming.size)]
                    .comments.appendList(Comment(Id(index), "USER $index", Email("ABC_$index@gmail.com"), "Comment body $index"))
                Thread.sleep(300)
            }
        }.start()

        Thread {
            val userSnapshot = users.snapshot()
            for(i in 0 .. 20) {
                val index = i + 1000
                val post = Post(
                    Id(i),
                    userSnapshot[Random.nextInt(userSnapshot.size)],
                    "TITLE $index",
                    "Body $index")
                posts.appendList(post)
                Thread.sleep(1500)
            }
        }.start()
    }

    override fun reload() {
        users.reset()
        posts.reset()

        loadAllData()
               .start()

    }


    @Subscribe
    fun loadComments(event : LoadCommentsEvent) {
        if (comments.compareAndSet(false, true)) {
            val combined = CombineFlatMap()
            loadCommentsObservable(combined)
            combined.onError { comments.set(false) }
            combined.start()
        }
    }

}