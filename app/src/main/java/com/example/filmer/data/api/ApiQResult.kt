package com.example.filmer.data.api

data class ApiQResult(
    val page: Int,
    val results: List<FilmResult>,
    val total_pages: Int,
    val total_results: Int
)