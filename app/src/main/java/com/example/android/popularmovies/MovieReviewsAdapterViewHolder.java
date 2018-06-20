package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
 * a cache of the child views for a movie item. It's also a convenient place to set an
 * OnClickListener, since it has access to the adapter and the views.
 */
class MovieReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final MovieReviewsAdapterOnClickHandler mClickHandler;
//    private final ImageView mImageView;

    MovieReviewsAdapterViewHolder(View view, MovieReviewsAdapterOnClickHandler clickHandler) {
        super(view);
        mClickHandler = clickHandler;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int adapterPosition = getAdapterPosition();
        mClickHandler.onReviewClick(adapterPosition);
    }
}
