package com.example.remote_module.entity

data class ApiQResult(
    val page: Int,
    val results: List<FilmResult>,
    val total_pages: Int,
    val total_results: Int
)