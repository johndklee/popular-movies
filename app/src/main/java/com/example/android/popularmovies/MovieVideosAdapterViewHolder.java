package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
 * a cache of the child views for a movie item. It's also a convenient place to set an
 * OnClickListener, since it has access to the adapter and the views.
 */
class MovieVideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final MovieVideosAdapterOnClickHandler mClickHandler;

    MovieVideosAdapterViewHolder(View view, MovieVideosAdapterOnClickHandler clickHandler) {
        super(view);
        mClickHandler = clickHandler;
        ImageView mVideoPlayButton = view.findViewById(R.id.video_play_button);
        mVideoPlayButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int adapterPosition = getAdapterPosition();
        mClickHandler.onVideoClick(adapterPosition);
    }
}
