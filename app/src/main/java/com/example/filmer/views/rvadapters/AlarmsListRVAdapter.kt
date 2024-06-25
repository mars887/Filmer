package com.example.filmer.views.rvadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filmer.App
import com.example.filmer.databinding.AlarmRvItemBinding
import com.example.sql_module.AlarmInfo
import com.example.sql_module.sql.AlarmsDao
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AlarmsListRVAdapter(
    private val clickListenerner: OnItemClickListener,
    private val leftClickListener: OnButtonClickListener,
    private val rightClickListener: OnButtonClickListener,
) :
    RecyclerView.Adapter<AlarmsListRVAdapter.AIRVHolder>() {

    lateinit var data: List<AlarmInfo>
    private var cmode: Pair<AlarmInfo, Int>? = null

    fun setModeFor(alarm: AlarmInfo?, mode: Int) {
        if(alarm != null) cmode = Pair(alarm, mode)
        else cmode = null
    }
    fun getCurrentMode(): Pair<AlarmInfo, Int>? {
        return cmode
    }

    @Inject
    lateinit var alarmsDao: AlarmsDao

    init {
        App.instance.appComponent.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AIRVHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlarmRvItemBinding.inflate(inflater, parent, false)

        return AIRVHolder(binding).also { it.setNormal() }
    }

    override fun onBindViewHolder(holder: AIRVHolder, position: Int) {
        val data1 = data[position]
        holder.alarmInfo = data1

        with(holder.binding) {

            Glide.with(root)
                .load(com.example.remote_module.entity.FilmApiConstants.IMAGES_URL + "w342" + data1.poster)
                .centerCrop()
                .into(posterImage)

            title.text = data1.filmTitle
            alarmDate.text = formatDateTime(LocalDateTime.parse(data1.alarmDate))
        }
        holder.itemView.setOnClickListener {
            clickListenerner.click(data1)
        }
        holder.binding.buttons1.setOnClickListener {
            leftClickListener.click()
        }
        holder.binding.buttons2.setOnClickListener {
            rightClickListener.click()
        }
        if (cmode != null) {
            if(data1 == cmode!!.first) {
                when (cmode!!.second) {
                    -1 -> holder.setDeleteMode()
                    0 -> holder.setNormal()
                    1 -> holder.setChangeDateMode()
                }
            } else holder.setNormal()
        } else holder.setNormal()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun interface OnItemClickListener {
        fun click(alarm: AlarmInfo)
    }

    fun interface OnButtonClickListener {
        fun click()
    }

    class AIRVHolder(val binding: AlarmRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var alarmInfo: AlarmInfo? = null

        init {
            println("creating airvHolder")
        }

        fun setNormal() {
            binding.alarmInfoContainer.setVisibilityForAll(View.VISIBLE)
            binding.alarmInfoButtonsContainer.setVisibilityForAll(View.INVISIBLE)
            println("setNormal ${alarmInfo?.filmTitle}")
        }

        fun setDeleteMode() {
            binding.alarmInfoContainer.setVisibilityForAll(View.INVISIBLE)
            binding.alarmInfoButtonsContainer.setVisibilityForAll(View.VISIBLE)
            binding.buttonsText.text = "delete?"
            binding.buttons1.text = "Delete!"
            binding.buttons2.text = "NO,no,no..."
            println("setDeleteMode ${alarmInfo?.filmTitle}")
        }

        fun setChangeDateMode() {
            binding.alarmInfoContainer.setVisibilityForAll(View.INVISIBLE)
            binding.alarmInfoButtonsContainer.setVisibilityForAll(View.VISIBLE)
            binding.buttonsText.text = "change date or time"
            binding.buttons1.text = "Date"
            binding.buttons2.text = "Time"
            println("setChangeDateMode ${alarmInfo?.filmTitle}")
        }
    }
}

private fun ViewGroup.setVisibilityForAll(visMode: Int) {
    visibility = visMode
    children.forEach { child ->
        if (child is ViewGroup) {
            child.setVisibilityForAll(visibility)
        } else {
            child.visibility = visibility
        }
    }
}
fun formatDateTime(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val datePart = dateTime.toLocalDate()
    val timePart = dateTime.toLocalTime()

    val daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), datePart)

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTime = timePart.format(timeFormatter)

    val dayDescription = when (daysBetween) {
        -1L -> "вчера"
        0L -> "сегодня"
        1L -> "завтра"
        2L -> "послезавтра"
        else -> datePart.format(DateTimeFormatter.ofPattern("d MMMM yyyy")) // например: 25 декабря 2023
    }

    return "$dayDescription в $formattedTime"
}