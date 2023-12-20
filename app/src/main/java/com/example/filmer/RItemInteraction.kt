package com.example.filmer

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class RItemInteraction(val adapter: RAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = false

    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.START) {
            adapter.data.removeAt(viewHolder.adapterPosition)
            adapter.data.add(MainActivity.getRandomRData())
            adapter.notifyItemRemoved(viewHolder.adapterPosition)
            adapter.notifyItemInserted(adapter.data.size - 1)
        } else {
            adapter.data[viewHolder.adapterPosition].isFavorite = !adapter.data[viewHolder.adapterPosition].isFavorite
            adapter.notifyItemChanged(viewHolder.adapterPosition)
        }
    }
}