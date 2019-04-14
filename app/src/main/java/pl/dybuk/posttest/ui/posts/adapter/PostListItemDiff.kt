package pl.dybuk.posttest.ui.posts.adapter


import androidx.recyclerview.widget.DiffUtil
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItem
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItemType

class PostListItemDiff(
    val newList: List<PostListItem>,
val oldList: List<PostListItem>) : DiffUtil.Callback() {

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
                (oldItem.type != PostListItemType.BASIC || oldItem.post?.id?.equals(newItem.post?.id) ?: false)

    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.type == newItem.type &&
                (oldItem.type != PostListItemType.BASIC || oldItem.post?.id?.equals(newItem.post?.id) ?: false)
    }

}