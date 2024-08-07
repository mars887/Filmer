package com.example.filmer.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmer.App
import com.example.filmer.R
import com.example.filmer.views.rvadapters.RAdapter
import com.example.filmer.views.rvadapters.RItemDecorator
import com.example.filmer.views.rvadapters.RItemInteraction
import com.example.filmer.util.FilmSearch
import com.example.sql_module.FilmData
import com.example.filmer.databinding.FragmentTvBinding
import com.example.filmer.util.AnimationHelper
import com.example.filmer.viewmodel.TVFragmentViewModel
import com.example.filmer.viewmodel.bindTo
import com.example.filmer.views.MainActivity
import dagger.Lazy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TVFragment(onlyFavorites: Boolean = false) : Fragment() {
    private lateinit var binding: FragmentTvBinding
    private lateinit var adapter: RAdapter

    val filmSearch: FilmSearch = FilmSearch()
    private var lastSearch: String? = null
    private var allInited = false

    @Inject
    lateinit var scope: CoroutineScope

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(TVFragmentViewModel::class.java)
    }

    private var filmsDataBase = listOf<FilmData>()
        set(value) {
            if (field == value) return
            field = value
            updateSearch()
        }

    var onlyFavorites: Boolean = onlyFavorites
        set(value) {
            field = value
            if (allInited) updateSearch()
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTvBinding.inflate(inflater, container, false)
        App.instance.appComponent.inject(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if(!App.instance.interactor.checkTrialState()) onlyFavorites = true

        viewModel.filmListData
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                filmsDataBase = it
            }.bindTo(viewModel)


        viewModel.showProgressBar
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                binding.pullToRefresh.isRefreshing = it
            }.bindTo(viewModel)


        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.tvFragmentRoot,
            requireActivity(),
            if (onlyFavorites) 2 else 1
        )

        val recyc = binding.RView

        recyc.layoutManager = LinearLayoutManager(requireContext())

        adapter = RAdapter(object : RAdapter.OnItemClickListener {
            override fun click(film: FilmData) {
                (requireActivity() as MainActivity).launchDetailsFragment(film)
            }
        })

        recyc.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (onlyFavorites) return
                if ((adapter.data.size - (binding.RView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()) < 20) {
                    if (lastSearch.isNullOrBlank()) viewModel.loadNewFilmList()
                    else viewModel.searchNewFilmList(lastSearch!!)
                }
            }
        })

        val dataList = ArrayList<FilmData>()
        dataList.addAll(filmsDataBase)

        adapter.data = dataList
        recyc.adapter = adapter
        recyc.addItemDecoration(RItemDecorator(5))

        val callback = RItemInteraction(binding.RView.adapter as RAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.RView)

        val anim =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recyc_animation_anim)
        recyc.layoutAnimation = anim

        initSearchView()

        initPullToRefresh()
        allInited = true
        updateSearch()

        MainActivity.detailsFilmIntent?.let { title ->
            scope.launch {
                viewModel.interactor.getFilmByTitle(title)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { film ->
                        MainActivity.instance.launchDetailsFragment(film)
                    }
            }
        }
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                lastSearch = query
                updateSearch()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (filmsDataBase.size < 1000) {
                    lastSearch = newText
                    updateSearch()
                }
                return true
            }
        })
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }
    }

    private fun updateSearch() {
        println("sup $lastSearch")
        if (onlyFavorites) {
            viewModel.interactor.dbase.lastLoadedFavorites?.toList()
                ?.let {
                    filmSearch.search(it, adapter, onlyFavorites, lastSearch)
                }
        } else {
            filmSearch.search(filmsDataBase, adapter, onlyFavorites, lastSearch)
            if (adapter.data.size < 20 && lastSearch.isNullOrBlank()) {
                viewModel.loadNewFilmList()
            } else if (!lastSearch.isNullOrBlank()) {
                viewModel.searchNewFilmList(lastSearch!!, true)
            }
        }
    }

    private fun initPullToRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            if (lastSearch.isNullOrBlank()) viewModel.loadNewFilmList(true)
            else viewModel.searchNewFilmList(lastSearch!!, true)
        }
    }

    fun countOfFavorites(): Int = adapter.data.count { it.isFavorite }
}