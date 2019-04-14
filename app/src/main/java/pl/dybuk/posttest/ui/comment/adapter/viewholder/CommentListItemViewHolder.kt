package pl.dybuk.posttest.ui.comment.adapter.viewholder

import android.view.View
import android.widget.TextView
import pl.dybuk.posttest.R
import pl.dybuk.posttest.ui.comment.adapter.item.CommentListItem

class CommentListItemViewHolder(view: View) : CommentListViewHolder(view) {

    var item : CommentListItem? = null

    val title by lazy { view.findViewById<TextView>(R.id.view_holder_comment_item_title) }

    val body by lazy { view.findViewById<TextView>(R.id.view_holder_comment_item_body) }

    val user by lazy { view.findViewById<TextView>(R.id.view_holder_comment_item_user) }

    override fun bind(item: CommentListItem) {
        this.item = item
        this.title.text = item.comment?.name
        this.body.text = item.comment?.body
        this.user.text = item.comment?.name
    }

}