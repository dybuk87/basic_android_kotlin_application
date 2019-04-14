package pl.dybuk.posttest.ui.comment.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.dybuk.posttest.ui.comment.adapter.item.CommentListItem

abstract class CommentListViewHolder(view :View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item : CommentListItem)

}
