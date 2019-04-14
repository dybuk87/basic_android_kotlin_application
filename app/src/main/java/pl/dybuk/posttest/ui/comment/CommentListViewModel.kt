package pl.dybuk.posttest.ui.comment

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.event.LoadCommentsEvent
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncListState

class CommentListViewModel : ViewModel() {
    val commentList : MutableLiveData<AsyncListState<Comment>> = MutableLiveData()

    val postTitle : MutableLiveData<String> = MutableLiveData()

    val postBody : MutableLiveData<String> = MutableLiveData()

    val postUser : MutableLiveData<String> = MutableLiveData()

    lateinit var chatRepository : ChatRepository

    var commentListConsumer : Disposable? = null

    var post : Post? = null

    lateinit var eventBus : EventBus

    fun subscribe(eventBus : EventBus, chatRepository: ChatRepository, postId : Id) {
        this.eventBus = eventBus
        this.chatRepository = chatRepository

        this.chatRepository.findPostById(postId).subscribe {
            this.post = it
            postTitle.postValue(it.title)
            postBody.postValue(it.body)
            postUser.postValue("${it.user.name} ${it.user.username}")
            eventBus.post(LoadCommentsEvent(it))
            Log.i("POST", "SUBSCRIBE POST: " + it.id.id)
            commentListConsumer = it.comments.state.subscribe { onCommentListUpdate(it) }
        }
    }

    fun unsubscribe() {
        commentListConsumer?.dispose()
        commentListConsumer = null
    }

    @VisibleForTesting
    fun onCommentListUpdate(commentList : AsyncListState<Comment>) {
        Log.i("POST", "UPDATE POST: " + commentList.type)
        this.commentList.postValue(commentList)
    }
}