package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.popularmovies.data.AppDatabase;
import com.example.android.popularmovies.data.MovieListItemEntry;
import com.example.android.popularmovies.data.MovieListType;

import java.util.List;

class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    private final LiveData<List<MovieListItemEntry>> mMyFavoriteMovieListItems;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        mMyFavoriteMovieListItems = database.movieListItemDao().loadMovieListItemsByMovieListType(MovieListType.SHOW_MY_FAVORITES);
    }

    public LiveData<List<MovieListItemEntry>> getMyFavoriteMovieListItems() {
        return mMyFavoriteMovieListItems;
    }

}

