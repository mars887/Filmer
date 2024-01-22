package com.example.filmer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filmer.databinding.FragmentTvBinding

class TVFragment(onlyFavorites: Boolean = false) : Fragment() {
    private lateinit var binding: FragmentTvBinding
    private lateinit var adapter: RAdapter
    var onlyFavorites: Boolean = onlyFavorites
        set(value) {
            field = value
            updateSearch()
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.tvFragmentRoot,
            requireActivity(),
            if (onlyFavorites) 2 else 1
        )

        val recyc = binding.RView
        recyc.layoutManager = LinearLayoutManager(requireContext())

        adapter = RAdapter(object : RAdapter.OnItemClickListener {
            override fun click(film: RData) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        val dataList = ArrayList<RData>()
        dataList.addAll(App.libraryData)

        adapter.data = dataList
        recyc.adapter = adapter
        recyc.addItemDecoration(RItemDecorator(5))

        val callback = RItemInteraction(binding.RView.adapter as RAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.RView)

        val anim =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recyc_animation_anim)
        recyc.layoutAnimation = anim

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Search.search(query, adapter, onlyFavorites)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (App.libraryData.size < 200) Search.search(newText, adapter, onlyFavorites)
                return true
            }
        })
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
    }

    private fun updateSearch() {
        Search.search(null, adapter, onlyFavorites)
    }

    fun countOfFavorites(): Int = adapter.data.count { it.isFavorite }
}