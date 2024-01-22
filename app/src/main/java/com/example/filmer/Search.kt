package com.example.filmer

class Search {
    companion object {

        fun search(text: String?, adapter: RAdapter, onlyFavorites: Boolean = false) {
            val list = adapter.data
            var origin = ArrayList<RData>()
            if (onlyFavorites) {
                App.libraryData.forEach {
                    if (it.isFavorite) origin.add(it)
                }
            } else {
                origin.addAll(App.libraryData)
            }
            if (!text.isNullOrBlank()) {
                origin = origin.filter {
                    it.title.lowercase().contains(text.lowercase()) ||
                            it.title.lowercase().startsWith(text.lowercase())
                } as ArrayList<RData>
            }

            var minus = 0
            App.libraryData.forEachIndexed { i, data ->
                if (origin.contains(data)) {
                    if (!list.contains(data)) {
                        list.add(i - minus, data)
                        adapter.notifyItemInserted(i - minus)
                    }
                } else if (!origin.contains(data)) {
                    if (list.contains(data)) {
                        list.removeAt(i - minus)
                        adapter.notifyItemRemoved(i - minus)
                    }
                    minus++
                }
            }
        }

    }
}