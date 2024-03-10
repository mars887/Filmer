package com.example.filmer.views.rvadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filmer.R
import com.example.filmer.data.api.FilmApiConstants
import com.example.filmer.data.FilmData
import com.example.filmer.databinding.RvItemBinding

class RAdapter(private val clickListenerner: OnItemClickListener, private val showIsFavorite: Boolean = true) :
    RecyclerView.Adapter<RAdapter.RViewHolder>() {

    var data: ArrayList<FilmData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemBinding.inflate(inflater, parent, false)

        return RViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RViewHolder, position: Int) {
        val data1 = data[position]

        with(holder.binding) {

            Glide.with(root)
                .load(FilmApiConstants.IMAGES_URL + "w342" + data1.poster)
                .centerCrop()
                .into(posterImage)

            val text = data1.title
            title.text = text
            description.text = data1.description
            if(showIsFavorite) {
                isFavorite.setImageResource(if (data1.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
                isFavorite.setOnClickListener {
                    data1.isFavorite = !data1.isFavorite
                    isFavorite.setImageResource(if (data1.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
                }
            } else isFavorite.isVisible = false

            ratingView.setProgress((data1.rating * 10).toInt())
        }
        holder.itemView.setOnClickListener {
            clickListenerner.click(data1)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener {
        fun click(film: FilmData)
    }

    class RViewHolder(val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root)
}
