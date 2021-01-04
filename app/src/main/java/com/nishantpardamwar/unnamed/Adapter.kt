package com.nishantpardamwar.unnamed

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nishantpardamwar.unnamed.network.responses.SearchItem
import kotlinx.android.synthetic.main.view_holder_item.view.*

class Adapter(private val onLoadMore: () -> Unit) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val list: MutableList<SearchItem> = arrayListOf()
    private var hasMore: Boolean = false

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (hasMore) {
                        onLoadMore.invoke()
                    }
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_holder_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position, list[position])
    }

    fun setNewList(newList: List<SearchItem>, hasMore: Boolean) {
        this.hasMore = hasMore
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun loadMore(newList: List<SearchItem>, hasMore: Boolean) {
        this.hasMore = hasMore
        val prevSize = list.size
        list.addAll(newList)
        notifyItemRangeInserted(prevSize, list.size)
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(position: Int, data: SearchItem) {
            itemView.textTitle.text = data.title
            itemView.textDesc.text = Html.fromHtml(data.snippet)
        }
    }
}