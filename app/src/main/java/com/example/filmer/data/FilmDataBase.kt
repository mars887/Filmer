package com.example.filmer.data

class FilmDataBase(val data : List<FilmData>) : DataBase<FilmData> {

    override fun getAll(): List<FilmData> {
        return data
    }

    override fun getByIndex(index: Int): FilmData {
        return data[index]
    }
}