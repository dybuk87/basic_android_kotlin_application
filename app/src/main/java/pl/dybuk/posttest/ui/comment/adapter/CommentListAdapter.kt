package pl.dybuk.posttest.ui.comment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import pl.dybuk.health.core.chat.entity.Comment
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.posttest.R
import pl.dybuk.posttest.ui.comment.adapter.item.CommentListItem
import pl.dybuk.posttest.ui.comment.adapter.item.CommentListItemType
import pl.dybuk.posttest.ui.comment.adapter.viewholder.*

class CommentListAdapter : RecyclerView.Adapter<CommentListViewHolder>() {

    companion object {
        const val BASIC_ID = 1
        const val EMPTY_ID = 2
        const val ERROR_ID = 3
        const val LOADER_ID = 4
    }

    var list: MutableList<CommentListItem> = mutableListOf()

    fun update(data: AsyncListState<Comment>) {
        when (data.type) {
            AsyncListState.StateType.NOT_READY -> setNotReadyState()
            AsyncListState.StateType.LOADING -> addLoadingItems()
            AsyncListState.StateType.INITIALIZED -> setData(data.snapshot)
            AsyncListState.StateType.ERROR -> setErrorState()
        }
    }

    @VisibleForTesting
    fun setErrorState() {
        list.clear()
        list.add(CommentListItem(CommentListItemType.ERROR))
        notifyDataSetChanged()
    }

    @VisibleForTesting fun setData(snapshot: List<Comment>) {
        removeLoadingItems()

        val converted =
            Stream.of(snapshot).map { CommentListItem(CommentListItemType.BASIC, it) }.collect(Collectors.toList())

        val diff = DiffUtil.calculateDiff(CommentListItemDiff(converted, list), true)

        list.clear()
        list.addAll(converted)

        if (!list.isEmpty()) {
            diff.dispatchUpdatesTo(this)
        } else {
            list.add(CommentListItem(CommentListItemType.EMPTY))
            notifyDataSetChanged()
        }


    }

    @VisibleForTesting fun addLoadingItems() {
        var pos = list.size
        for (i in 0..3) {
            list.add(CommentListItem(CommentListItemType.LOADER))
        }
        notifyItemRangeInserted(pos, 4)
    }

    @VisibleForTesting fun removeLoadingItems() {
        if (!list.isEmpty()) {
            for (i in list.size - 1..0) {
                var item = list[i]
                if (item.type == CommentListItemType.LOADER) {
                    list.remove(item)
                    notifyItemRemoved(i)
                }
            }
        }
    }

    @VisibleForTesting fun setNotReadyState() {
        list.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (list[position].type) {
            CommentListItemType.BASIC -> BASIC_ID
            CommentListItemType.EMPTY -> EMPTY_ID
            CommentListItemType.ERROR -> ERROR_ID
            CommentListItemType.LOADER -> LOADER_ID
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentListViewHolder =
        when (viewType) {
            BASIC_ID -> CommentListItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_comment_item,
                    parent,
                    false
                )
            )
            LOADER_ID -> CommentListLoaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_loader,
                    parent,
                    false
                )
            )
            ERROR_ID -> CommentListErrorViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_error,
                    parent,
                    false
                )
            )
            EMPTY_ID -> CommentListEmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_empty,
                    parent,
                    false
                )
            )
            else -> CommentListItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_post_item,
                    parent,
                    false
                )
            )
        }


    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(viewHolder: CommentListViewHolder, index: Int) = viewHolder.bind(list[index])

}