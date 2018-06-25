package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.app.LoaderManager;

interface MoviesAdapterUI {

    void showErrorDisplay();

    void showResult();

    void showProgressBar();

    void hideProgressBar();

    Context getContext();

    @SuppressWarnings("EmptyMethod")
    LoaderManager getSupportLoaderManager();

}
