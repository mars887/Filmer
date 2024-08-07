package com.example.remote_module

import com.example.remote_module.entity.ApiQResult
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmApi {
    @GET("movie/{category}")
    fun getFilms(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Observable<ApiQResult>

    @GET("search/movie")
    fun getFilmsSearch(
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Observable<ApiQResult>

    @GET("movie/{movieId}")
    fun getFilmById(
        @Path("movieId") movieId: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Observable<ApiMovieResult>
}