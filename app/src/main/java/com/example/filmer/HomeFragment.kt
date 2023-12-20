package com.example.filmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmer.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: RAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyc = binding.RView
        recyc.layoutManager = LinearLayoutManager(requireContext())

        adapter = RAdapter(object : RAdapter.OnItemClickListener {
            override fun click(film: RData) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        adapter.data = (requireActivity() as MainActivity).getData()
        recyc.adapter = adapter
        recyc.addItemDecoration(RItemDecorator(5))

        val callback = RItemInteraction(binding.RView.adapter as RAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.RView)
    }
}