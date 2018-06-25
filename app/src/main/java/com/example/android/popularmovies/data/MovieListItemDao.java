package com.example.android.popularmovies.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieListItemDao {

    @Insert
    void insertMovieListItem(MovieListItemEntry movie_list_item);

    @Delete
    void deleteMovieListItem(MovieListItemEntry movie_list_item);

    @Query("SELECT * FROM movie_list_item WHERE movieListType = :movie_list_type")
    List<MovieListItemEntry> loadMovieListItemsByMovieListType(String movie_list_type);

    @Query("SELECT * FROM movie_list_item WHERE movieListType = :movie_list_type AND movieID = :movie_id")
    List<MovieListItemEntry> findMovieItemByPKs(String movie_list_type, String movie_id);

}
