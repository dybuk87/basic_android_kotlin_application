package pl.dybuk.posttest.ui.comment.adapter

import androidx.recyclerview.widget.DiffUtil
import pl.dybuk.posttest.ui.comment.adapter.item.CommentListItem
import pl.dybuk.posttest.ui.comment.adapter.item.CommentListItemType
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItem
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItemType

class CommentListItemDiff(
    val newList: List<CommentListItem>,
    val oldList: List<CommentListItem>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.type == newItem.type &&
                (oldItem.type != CommentListItemType.BASIC || oldItem.comment?.id?.equals(newItem.comment?.id) ?: false)

    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.type == newItem.type &&
                (oldItem.type != CommentListItemType.BASIC || oldItem.comment?.id?.equals(newItem.comment?.id) ?: false)
    }

}