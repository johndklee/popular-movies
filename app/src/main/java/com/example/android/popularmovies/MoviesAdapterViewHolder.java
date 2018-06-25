package com.example.android.popularmovies;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
 * a cache of the child views for a movie item. It's also a convenient place to set an
 * OnClickListener, since it has access to the adapter and the views.
 */
class MoviesAdapterViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private static final String TAG = MoviesAdapterViewHolder.class.getSimpleName();

    private final MoviesAdapterOnClickHandler mClickHandler;

    MoviesAdapterViewHolder(View view, MoviesAdapterOnClickHandler clickHandler) {
        super(view);
        mClickHandler = clickHandler;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int adapterPosition = getAdapterPosition();
        Log.d(TAG, "onClick called at position = "+adapterPosition);
        mClickHandler.onClick(adapterPosition);
    }

}
