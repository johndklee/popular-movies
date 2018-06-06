package com.example.android.popularmovies;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
 * a cache of the child views for a movie item. It's also a convenient place to set an
 * OnClickListener, since it has access to the adapter and the views.
 */
class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    final MovieAdapterOnClickHandler mClickHandler;
    final ImageView imageView;

    MoviesAdapterViewHolder(View view, MovieAdapterOnClickHandler clickHandler) {
        super(view);
        mClickHandler = clickHandler;
        imageView = view.findViewById(R.id.movie_image_view);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int adapterPosition = getAdapterPosition();
        mClickHandler.onClick(adapterPosition);
    }
}
