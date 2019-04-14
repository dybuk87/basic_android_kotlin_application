package pl.dybuk.posttest.ui.posts


import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annimon.stream.function.Predicate

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.posttest.R
import pl.dybuk.posttest.ui.base.BaseActivity
import pl.dybuk.posttest.ui.comment.CommentListActivity
import pl.dybuk.posttest.ui.dagger
import pl.dybuk.posttest.ui.posts.adapter.PostListAdapter
import pl.dybuk.posttest.ui.posts.event.OpenPostEvent
import javax.inject.Inject

class PostListActivity : BaseActivity() {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var eventBus: EventBus

    lateinit var postListViewModel: PostListViewModel

    val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.activity_post_list_recycler_list) }

    val search : EditText by lazy { findViewById<EditText> (R.id.activity_post_list_search) }

    lateinit var postListAdapter: PostListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_post_list)

        postListViewModel = ViewModelProviders.of(this).get(PostListViewModel::class.java)

        dagger().inject(this)

        postListViewModel.subscribe(chatRepository)

        postListAdapter = PostListAdapter(eventBus)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postListAdapter

        postListViewModel.postList.observe(this, Observer { postListUpdate(it) })
        postListViewModel.searchFilter.observe(this, Observer { updateFilterField(it) })

        eventBus.register(this)

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                postListViewModel.updateTestFilter(text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }

    private fun updateFilterField(value: String?) {
        postListAdapter.updateFilter(Predicate {
            val validValue = value ?: ""
             (it.post?.title?.contains(validValue, true) ?: false) ||
                    (it.post?.body?.contains(validValue, true) ?: false) ||
                    (it.post?.user?.name?.contains(validValue, true) ?: false)
        })
    }


    @Subscribe
    fun onOpenPostEvent(openPostEvent: OpenPostEvent) {
        CommentListActivity.open(this, openPostEvent.post)
    }


    private fun postListUpdate(state: AsyncListState<Post>?) {
        state?.let { this.postListAdapter.update(it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        postListViewModel.unsubscribe()

        eventBus.unregister(this)
    }


}