package com.example.filmer.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FilmApi {
    @GET("movie/popular")
    fun getPopular(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Call<ApiQResult>
}