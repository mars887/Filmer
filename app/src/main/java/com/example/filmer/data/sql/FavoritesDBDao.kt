package com.example.filmer.data.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Observable

@Dao
interface FavoritesDBDao {

    @Query("SELECT * FROM favorite_films")
    fun getAllFavorites(): Observable<List<FavoriteFilmData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(film: FavoriteFilmData)

    @Delete
    fun deleteFavorite(film: FavoriteFilmData)
}