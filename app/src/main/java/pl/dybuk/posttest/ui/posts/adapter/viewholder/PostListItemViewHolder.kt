package pl.dybuk.posttest.ui.posts.adapter.viewholder

import android.view.View
import android.widget.TextView
import org.greenrobot.eventbus.EventBus
import pl.dybuk.posttest.R
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItem
import pl.dybuk.posttest.ui.posts.event.OpenPostEvent

class PostListItemViewHolder(val eventBus: EventBus, view : View) : PostListViewHolder(view) {

    var item : PostListItem? = null

    val title by lazy { view.findViewById<TextView>(R.id.view_holder_post_item_title) }

    val body by lazy { view.findViewById<TextView>(R.id.view_holder_post_item_body) }

    val user by lazy { view.findViewById<TextView>(R.id.view_holder_post_item_user_name) }

    init {
        view.setOnClickListener {  item?.post?. let { eventBus.post(OpenPostEvent(it)) }}
    }

    override fun bind(item: PostListItem) {
        this.item = item
        this.title.text = item?.post?.title
        this.body.text = item?.post?.body
        this.user.text = "${item?.post?.user?.name} ${item?.post?.user?.username}"
    }




}