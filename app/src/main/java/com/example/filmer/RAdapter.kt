package com.example.filmer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.filmer.databinding.RvItemBinding

class RAdapter(private val clickListenerner: OnItemClickListener) :
    RecyclerView.Adapter<RAdapter.RViewHolder>() {

    var data: ArrayList<RData> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemBinding.inflate(inflater, parent, false)

        return RViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RViewHolder, position: Int) {
        val data1 = data[position]

        with(holder.binding) {
            posterImage.setImageResource(data1.posterId)
            title.text = data1.title
            description.text = data1.description
            isFavorite.setImageResource(if (data1.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
            isFavorite.setOnClickListener {
                data1.isFavorite = !data1.isFavorite
                isFavorite.setImageResource(if (data1.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
            }
        }
        holder.itemView.setOnClickListener {
            clickListenerner.click(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener {
        fun click(film: RData)
    }

    class RViewHolder(val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root)
}
