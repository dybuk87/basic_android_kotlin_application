package pl.dybuk.posttest.ui.posts.adapter.viewholder


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItem

abstract class PostListViewHolder(view :View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item : PostListItem)


}
