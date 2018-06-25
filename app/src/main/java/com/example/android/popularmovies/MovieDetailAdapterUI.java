package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.LoaderManager;

interface MovieDetailAdapterUI {

    void showErrorDisplay();

    void hideErrorDisplay();

    void showProgressBar();

    void hideProgressBar();

    Context getContext();

    Activity getActivity();

    @SuppressWarnings("EmptyMethod")
    LoaderManager getSupportLoaderManager();

    void setVideoCount(int video_count);

}
