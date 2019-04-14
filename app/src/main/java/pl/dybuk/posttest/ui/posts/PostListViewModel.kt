package pl.dybuk.posttest.ui.posts


import android.text.Editable
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.common.list.AsyncListState


class PostListViewModel : ViewModel() {

    val postList: MutableLiveData<AsyncListState<Post>> = MutableLiveData()

    val searchFilter: MutableLiveData<String> = MutableLiveData()

    var chatRepository: ChatRepository? = null

    var postListConsumer: Disposable? = null

    fun subscribe(chatRepository: ChatRepository) {
        this.chatRepository = chatRepository
        postListConsumer = this.chatRepository?.getAllPosts()?.state?.subscribe(
            { onPostListUpdate(it) },
            { it.printStackTrace() })
    }

    fun unsubscribe() {
        postListConsumer?.dispose()
        postListConsumer = null
    }

    @VisibleForTesting
    fun onPostListUpdate(postList: AsyncListState<Post>) {
        this.postList.postValue(postList)
    }

    fun updateTestFilter(text: String) {
        searchFilter.value = text
    }


}