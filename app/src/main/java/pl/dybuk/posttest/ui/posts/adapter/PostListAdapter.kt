package pl.dybuk.posttest.ui.posts.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import com.annimon.stream.function.Predicate
import org.greenrobot.eventbus.EventBus
import pl.dybuk.health.core.chat.entity.Post
import pl.dybuk.health.core.common.list.AsyncListState
import pl.dybuk.posttest.R
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItem
import pl.dybuk.posttest.ui.posts.adapter.item.PostListItemType
import pl.dybuk.posttest.ui.posts.adapter.viewholder.PostListEmptyViewHolder
import pl.dybuk.posttest.ui.posts.adapter.viewholder.PostListItemViewHolder
import pl.dybuk.posttest.ui.posts.adapter.viewholder.PostListLoaderViewHolder
import pl.dybuk.posttest.ui.posts.adapter.viewholder.PostListViewHolder


class PostListAdapter(val eventBus : EventBus) : RecyclerView.Adapter<PostListViewHolder>() {

    companion object {
        const val BASIC_ID = 1
        const val EMPTY_ID = 2
        const val ERROR_ID = 3
        const val LOADER_ID = 4
    }

    var all: MutableList<PostListItem> = mutableListOf()

    var filtered : MutableList<PostListItem> = mutableListOf()

    var filter : Predicate<PostListItem> = Predicate { true }

    fun update(data: AsyncListState<Post>) {
        when (data.type) {
            AsyncListState.StateType.NOT_READY -> setNotReadyState()
            AsyncListState.StateType.LOADING -> addLoadingItems()
            AsyncListState.StateType.INITIALIZED -> setData(data.snapshot)
            AsyncListState.StateType.ERROR -> setErrorState()
        }
    }

    @VisibleForTesting fun setErrorState() {
        all.clear()
        filtered.clear()
        filtered.add(PostListItem(PostListItemType.ERROR))
        notifyDataSetChanged()
    }

    @VisibleForTesting
    fun setData(snapshot: List<Post>) {
        removeLoadingItems()

        all =
            Stream.of(snapshot).map { PostListItem(PostListItemType.BASIC, it) }.collect(Collectors.toList())

        updateFilter(this.filter)



    }

    fun updateFilter(filter: Predicate<PostListItem>) {

        this.filter = filter

        val newFiltered = Stream.of(all).filter(filter).collect(Collectors.toList())

        val diff = DiffUtil.calculateDiff(PostListItemDiff(newFiltered, filtered), true)

        filtered.clear()
        filtered.addAll(newFiltered)

        if (!filtered.isEmpty()) {
            diff.dispatchUpdatesTo(this)
        } else {
            filtered.add(PostListItem(PostListItemType.EMPTY))
            notifyDataSetChanged()
        }

    }

    @VisibleForTesting fun addLoadingItems() {
        var pos = filtered.size
        for (i in 0..3) {
            filtered.add(PostListItem(PostListItemType.LOADER))
        }
        notifyItemRangeInserted(pos, 4)
    }

    @VisibleForTesting fun removeLoadingItems() {
        if (!filtered.isEmpty()) {
            for (i in filtered.size - 1..0) {
                var item = filtered[i]
                if (item.type == PostListItemType.LOADER) {
                    filtered.remove(item)
                    notifyItemRemoved(i)
                }
            }
        }
    }

    @VisibleForTesting fun setNotReadyState() {
        filtered.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (filtered[position].type) {
            PostListItemType.BASIC -> BASIC_ID
            PostListItemType.EMPTY -> EMPTY_ID
            PostListItemType.ERROR -> ERROR_ID
            PostListItemType.LOADER -> LOADER_ID
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder =
        when (viewType) {
            BASIC_ID -> PostListItemViewHolder(eventBus,
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_post_item,
                    parent,
                    false
                )
            )
            LOADER_ID -> PostListLoaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_loader,
                    parent,
                    false
                )
            )
            ERROR_ID -> PostListEmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_error,
                    parent,
                    false
                )
            )
            EMPTY_ID -> PostListEmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_empty,
                    parent,
                    false
                )
            )
            else -> PostListItemViewHolder(eventBus,
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_holder_post_item,
                    parent,
                    false
                )
            )
        }


    override fun getItemCount(): Int = filtered.size

    override fun onBindViewHolder(viewHolder: PostListViewHolder, index: Int) = viewHolder.bind(filtered[index])

    fun setFilter() {

    }

}