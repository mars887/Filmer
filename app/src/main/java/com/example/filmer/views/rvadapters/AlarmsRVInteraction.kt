package com.example.filmer.views.rvadapters

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.sql_module.AlarmInfo

class AlarmsRVInteraction(
    private val adapter: AlarmsListRVAdapter,
    val listener: (AlarmInfo, ViewHolder, Int) -> Unit,
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = false
    override fun isItemViewSwipeEnabled() = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        println("${adapter.data[viewHolder.adapterPosition].filmTitle} swiped to $direction")
        if (direction == ItemTouchHelper.START)
            listener(adapter.data[viewHolder.adapterPosition], viewHolder, -1)
        else if (direction == ItemTouchHelper.END)
            listener(adapter.data[viewHolder.adapterPosition], viewHolder, 1)
    }

    override fun getSwipeThreshold(viewHolder: ViewHolder): Float {
        return threshold
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    companion object {
        const val threshold = 0.5f
    }
}