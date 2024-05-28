package com.example.filmer.views.rvadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filmer.App
import com.example.filmer.R
import com.example.sql_module.FilmData
import com.example.filmer.data.db.SQLInteractor
import com.example.filmer.databinding.RvItemBinding
import javax.inject.Inject

class RAdapter(
    private val clickListenerner: OnItemClickListener,
    private val showIsFavorite: Boolean = true
) :
    RecyclerView.Adapter<RAdapter.RViewHolder>() {
    var data: ArrayList<com.example.sql_module.FilmData> = ArrayList()
    @Inject
    lateinit var sqlInteractor: SQLInteractor

    init {
        App.instance.appComponent.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemBinding.inflate(inflater, parent, false)

        return RViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RViewHolder, position: Int) {
        val data1 = data[position]

        with(holder.binding) {

            Glide.with(root)
                .load(com.example.remote_module.entity.FilmApiConstants.IMAGES_URL + "w342" + data1.poster)
                .centerCrop()
                .into(posterImage)

            val text = data1.title
            title.text = text
            description.text = data1.description
            if (showIsFavorite) {
                isFavorite.setImageResource(if (data1.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
                isFavorite.setOnClickListener {
                    data1.isFavorite = !data1.isFavorite
                    isFavorite.setImageResource(if (data1.isFavorite) R.drawable.baseline_favorite_24 else R.drawable.bottom_bar_favorite_icon)
                    if (data1.isFavorite) sqlInteractor.addToFavorites(data1)
                    else sqlInteractor.removeFromFavorites(data1)
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
        fun click(film: com.example.sql_module.FilmData)
    }

    class RViewHolder(val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root)
}
