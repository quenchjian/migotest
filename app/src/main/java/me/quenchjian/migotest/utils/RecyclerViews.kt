package me.quenchjian.migotest.utils

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerListAdapter<T, VH : RecyclerView.ViewHolder>(
  private val itemComparator: (oldItem: T, newItem: T) -> Boolean,
  private val contentComparator: (oldItem: T, newItem: T) -> Boolean,
  private val viewHolderCreator: (parent: ViewGroup, viewType: Int) -> VH,
) : ListAdapter<T, VH>(object : DiffUtil.ItemCallback<T>() {
  override fun areItemsTheSame(oldItem: T, newItem: T) = itemComparator(oldItem, newItem)
  override fun areContentsTheSame(oldItem: T, newItem: T) = contentComparator(oldItem, newItem)
}) {

  private var itemClick: (T) -> Unit = {}

  final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val holder = viewHolderCreator(parent, viewType)
    holder.itemView.setOnClickListener { itemClick(getItem(holder.adapterPosition)) }
    onViewHolderCreated(holder)
    return holder
  }

  final override fun onBindViewHolder(holder: VH, position: Int) {
    onBindViewHolder(holder, getItem(position))
  }

  open fun onViewHolderCreated(holder: VH) {}
  open fun onBindViewHolder(holder: VH, item: T) {}

  fun onItemClick(click: (T) -> Unit) = apply { itemClick = click }

  fun add(item: T) {
    val data = currentList.toMutableList()
    data.add(item)
    submitList(data)
  }

  fun add(items: List<T>) {
    if (items.isEmpty()) return
    val data = currentList.toMutableList()
    data.addAll(items)
    submitList(data)
  }

  fun replace(items: List<T>) {
    submitList(items)
  }
}

abstract class RecyclerAdapter<T, VH : RecyclerView.ViewHolder>(
  private val viewHolderCreator: (parent: ViewGroup, viewType: Int) -> VH,
) : RecyclerView.Adapter<VH>() {

  private val data = mutableListOf<T>()
  private var itemClick: (T) -> Unit = {}

  final override fun getItemCount(): Int = data.size

  final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    val holder = viewHolderCreator(parent, viewType)
    holder.itemView.setOnClickListener { itemClick(data[holder.adapterPosition]) }
    onViewHolderCreated(holder)
    return holder
  }

  final override fun onBindViewHolder(holder: VH, position: Int) {
    onBindViewHolder(holder, data[position])
  }

  open fun onViewHolderCreated(holder: VH) {}
  open fun onBindViewHolder(holder: VH, item: T) {}

  fun onItemClick(click: (T) -> Unit) = apply { itemClick = click }

  fun add(item: T) {
    data.add(item)
    notifyItemInserted(data.size - 1)
  }

  fun add(items: List<T>) {
    if (items.isEmpty()) return
    val sz = items.size
    val osz = itemCount
    data.addAll(items)
    notifyItemRangeInserted(osz, sz)
  }

  @SuppressLint("NotifyDataSetChanged")
  fun replace(items: List<T>) {
    if (items.isEmpty()) return
    data.clear()
    data.addAll(items)
    notifyDataSetChanged()
  }
}