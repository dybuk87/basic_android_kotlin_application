package pl.dybuk.posttest.ui.comment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import pl.dybuk.health.core.chat.ChatRepository
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.chat.value.Id
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.posttest.R
import pl.dybuk.posttest.ui.base.BaseActivity
import pl.dybuk.posttest.ui.comment.adapter.CommentListAdapter
import pl.dybuk.posttest.ui.dagger
import javax.inject.Inject

class CommentListActivity : BaseActivity() {

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var eventBus: EventBus

    lateinit var commentListViewModel: CommentListViewModel

    val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.activity_comment_list_recycler_list) }

    val postTitle: TextView by lazy { findViewById<TextView>(R.id.activity_comment_list_post_title) }
    val postBody: TextView by lazy { findViewById<TextView>(R.id.activity_comment_list_post_body) }
    val postUser: TextView by lazy { findViewById<TextView>(R.id.activity_comment_list_post_user) }

    lateinit var commentListAdapter: CommentListAdapter

    companion object {
        const val POST_ID = "post_id"

        fun open(context: Context, post: Post) {
            val intent = Intent(context, CommentListActivity::class.java)
            intent.putExtra(POST_ID, post.id)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_comment_list)

        var id: Id? = null

        if (intent.hasExtra(POST_ID)) {
            id = intent.getSerializableExtra(POST_ID) as Id?
        }

        commentListViewModel = ViewModelProviders.of(this).get(CommentListViewModel::class.java)

        dagger().inject(this)

        id?.let { commentListViewModel.subscribe(eventBus, chatRepository, it) }

        commentListAdapter = CommentListAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = commentListAdapter

        commentListViewModel.commentList.observe(this, Observer { postListUpdate(it) })

        commentListViewModel.postTitle.observe(this, Observer { postTitle.text = it })
        commentListViewModel.postBody.observe(this, Observer { postBody.text = it })
        commentListViewModel.postUser.observe(this, Observer { postUser.text = it })


    }

    @VisibleForTesting
    fun postListUpdate(state: AsyncListState<Comment>?) {
        state?.let { this.commentListAdapter.update(it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        commentListViewModel.unsubscribe()
    }

}