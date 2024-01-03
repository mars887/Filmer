package com.example.filmer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmer.databinding.FragmentFavoritesBinding
import java.util.stream.Collectors

class FavoritesFragment : Fragment(), HasSearch {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var adapter: RAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyc = binding.recyclerView
        recyc.layoutManager = LinearLayoutManager(requireContext())

        adapter = RAdapter(object : RAdapter.OnItemClickListener {
            override fun click(film: RData) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        }, showIsFavorite = false)

        val dataForAdapter: ArrayList<RData> = ArrayList()
        (requireActivity() as MainActivity).getData().filter { it.isFavorite }
            .forEach(dataForAdapter::add)

        adapter.data = dataForAdapter
        recyc.adapter = adapter
        recyc.addItemDecoration(RItemDecorator(5))

//        val callback = RItemInteraction(binding.recyclerView.adapter as RAdapter)
//        val touchHelper = ItemTouchHelper(callback)
//        touchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onQueryTextSubmit(text: String?) {
        if (text == null) adapter.data = (requireActivity() as MainActivity).getData()
        adapter.data = search(text!!, (requireActivity() as MainActivity).getData())
        adapter.notifyDataSetChanged()
    }

    override fun onQueryTextChange(text: String?) {
        if (text == null) adapter.data = (requireActivity() as MainActivity).getData()
        adapter.data = search(text!!, (requireActivity() as MainActivity).getData())
        adapter.notifyDataSetChanged()
    }


}