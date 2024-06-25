package com.example.filmer.views.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmer.R
import com.example.filmer.databinding.FragmentWatchLaterBinding
import com.example.filmer.util.AnimationHelper
import com.example.filmer.viewmodel.WatchLaterViewModel
import com.example.filmer.viewmodel.bindTo
import com.example.filmer.views.MainActivity
import com.example.filmer.views.rvadapters.AlarmsListRVAdapter
import com.example.filmer.views.rvadapters.AlarmsRVInteraction
import com.example.filmer.views.rvadapters.RItemDecorator
import com.example.sql_module.AlarmInfo
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime

class WatchLaterFragment : Fragment() {
    private lateinit var binding: FragmentWatchLaterBinding

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(WatchLaterViewModel::class.java)
    }

    var alarmsList: List<AlarmInfo> = listOf()
        set(value) {
            field = value
            updateAdapterData()
        }

    private lateinit var adapter: AlarmsListRVAdapter

    private var lastSwipedItem: Pair3<AlarmInfo, Int, Int>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.homeFragmentRoot,
            requireActivity(),
            3
        )

        viewModel.alarmsDatabase
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                alarmsList = it
                binding.RView.adapter?.notifyDataSetChanged()
            }.bindTo(viewModel)

        val recyc = binding.RView

        recyc.layoutManager = LinearLayoutManager(requireContext())

        adapter = AlarmsListRVAdapter({ alarmInfo ->                                        // item pressed
            MainActivity.checkInstance()?.launchDetailsFragment(alarmInfo.toFilmData())
        }, {                                                                                // left button pressed
            if (lastSwipedItem != null) {
                if (lastSwipedItem!!.third == -1) {
                    deleteAlarmInfo(lastSwipedItem!!.first)
                } else {

                    val time = getTimeArrayByAlarmInfo(lastSwipedItem!!.first)

                    DatePickerDialog(
                        this.requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                            time[0] = selectedYear
                            time[1] = selectedMonth
                            time[2] = selectedDay

                            updateAlarmDateByTimeArray(time, lastSwipedItem!!.first)

                        }, time[0], time[1], time[2]
                    ).show()
                }
            }
                    }, {                                                                    // right button pressed
            if (lastSwipedItem != null) {
                if (lastSwipedItem!!.third == -1) {
                    clearSwipeItem()
                } else {

                    val time = getTimeArrayByAlarmInfo(lastSwipedItem!!.first)

                    TimePickerDialog(
                        this.requireContext(), { _, selectedHour, selectedMinute ->
                            time[3] = selectedHour
                            time[4] = selectedMinute

                            updateAlarmDateByTimeArray(time, lastSwipedItem!!.first)

                        }, time[3], time[4], true
                    ).show()
                }
            }
        })

        recyc.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            clearSwipeItem()
        }

        recyc.adapter = adapter
        recyc.addItemDecoration(RItemDecorator(5))

        val callback =
            AlarmsRVInteraction(binding.RView.adapter as AlarmsListRVAdapter) { alarmInfo, viewHolder, direction ->
                val holder = viewHolder as AlarmsListRVAdapter.AIRVHolder

                if (direction == -1) {
                    if (LocalDateTime.parse(alarmInfo.alarmDate).isBefore(LocalDateTime.now())) {
                        deleteAlarmInfo(alarmInfo)
                    } else {
                        adapter.setModeFor(alarmInfo, direction)
                        adapter.notifyItemChanged(holder.adapterPosition)
                    }
                } else {
                    adapter.setModeFor(alarmInfo, direction)
                    adapter.notifyItemChanged(holder.adapterPosition)
                }

                if (lastSwipedItem != null && lastSwipedItem?.second != holder.adapterPosition) {
                    adapter.notifyItemChanged(lastSwipedItem!!.second)
                }

                lastSwipedItem = Pair3(alarmInfo, holder.adapterPosition, direction)
            }
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.RView)

        val anim =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recyc_animation_anim)
        recyc.layoutAnimation = anim

        updateAdapterData()
    }

    private fun updateAlarmDateByTimeArray(time: java.util.ArrayList<Int>, alarmInfo: AlarmInfo) {
        val selectedDateTime: LocalDateTime =
            LocalDateTime.of(time[0], time[1], time[2], time[3], time[4])

        viewModel.updateAlarmDate(alarmInfo, selectedDateTime.toString())
    }

    private fun getTimeArrayByAlarmInfo(alarmInfo: AlarmInfo): ArrayList<Int> {
        val alarmDate = LocalDateTime.parse(alarmInfo.alarmDate)
        return arrayListOf(
            alarmDate.year,
            alarmDate.monthValue,
            alarmDate.dayOfMonth,
            alarmDate.hour,
            alarmDate.minute
        )
    }

    private fun clearSwipeItem() {
        adapter.setModeFor(null, 0)
        if (lastSwipedItem != null) {
            adapter.notifyItemChanged(lastSwipedItem!!.second)
            lastSwipedItem = null
        }
    }

    private fun deleteAlarmInfo(alarm: AlarmInfo) {
        adapter.data.indexOf(alarm).takeIf { it != -1 }?.let {
            adapter.notifyItemRemoved(it)
        }
        viewModel.removeAlarm(alarm)
    }

    private fun updateAdapterData() {
        if (::adapter.isInitialized) {
            adapter.data = arrayListOf<AlarmInfo>().also {
                it.addAll(alarmsList)
            }
        }
    }

    data class Pair3<A, B, C>(
        val first: A,
        val second: B,
        val third: C
    )
}