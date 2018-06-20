package com.example.android.popularmovies;

import org.json.JSONObject;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieDetailsResult;
import com.example.android.popularmovies.utils.JSONUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

class MovieDetailAdapter
        implements LoaderManager.LoaderCallbacks<JSONObject> {

    private static final String TAG = MovieDetailAdapter.class.getSimpleName();

    private static final int ID_MOVIE_DETAIL_LOADER = 45;

    private final MovieDetailActivity mMovieDetailActivity;

    private String mMovieID;
    MovieDetailAdapter(MovieDetailActivity movieDetailActivity) {
        mMovieDetailActivity = movieDetailActivity;
    }

    public void setMovieID(String movie_id) {
        mMovieID = movie_id;
        loadData();
    }

    private void loadData() {
        LoaderManager lm = getMovieDetailActivity().getSupportLoaderManager();
        Loader l = lm.getLoader(ID_MOVIE_DETAIL_LOADER);
        if (l == null) {
            lm.initLoader(ID_MOVIE_DETAIL_LOADER, null, this);
        } else if (!lm.hasRunningLoaders()){
            lm.restartLoader(ID_MOVIE_DETAIL_LOADER, null, this);
        } else {
            Log.d(TAG, "wait for loader to finish");
        }
    }

    private String getMovieID() {
        return mMovieID;
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader called");
        return new MovieDetailAdapter.MyAsyncTaskLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject json) {
        Log.d(TAG, "onLoadFinished called");
        getMovieDetailActivity().hideProgressBar();
        try {
            if (json == null) {
                throw new Throwable("json object is null");
            }
            Log.d(TAG, "got "+json);
            MovieDetailsResult mMovieDetailsResult = JSONUtils.createMovieDetailsResult(json, getMovieDetailActivity());
            Log.d(TAG," = "+mMovieDetailsResult);

            String image_path = mMovieDetailsResult.getFullPosterPath();
            ImageView image_view = getMovieDetailActivity().findViewById(R.id.movie_poster);
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(image_view);

            TextView title = getMovieDetailActivity().findViewById(R.id.movie_title);
            title.setText(mMovieDetailsResult.original_title);

            TextView movie_release_year = getMovieDetailActivity().findViewById(R.id.movie_release_year);
            movie_release_year.setText(mMovieDetailsResult.getReleaseYear());

            TextView overview = getMovieDetailActivity().findViewById(R.id.movie_overview);
            overview.setText(mMovieDetailsResult.overview);

            TextView movie_average_rating = getMovieDetailActivity().findViewById(R.id.movie_average_rating);
            movie_average_rating.setText(mMovieDetailsResult.getVoteAverageOutOf10());

            TextView movie_length_minutes = getMovieDetailActivity().findViewById(R.id.movie_length_minutes);
            String min = getMovieDetailActivity().getResources().getString(R.string.movie_video_length);
            Resources rs = mMovieDetailActivity.getResources();
            movie_length_minutes.setText(rs.getString(R.string.movie_video_length,
                    mMovieDetailsResult.getMovieLengthInMinutes()));

        } catch (Throwable e) {
            Log.e(TAG, "failed to load data", e);
        }
        if (json == null) {
            getMovieDetailActivity().showErrorDisplay();
        } else {
            getMovieDetailActivity().hideErrorDisplay();
        }
    }

    private MovieDetailActivity getMovieDetailActivity() {
        return mMovieDetailActivity;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {
        Log.d(TAG, "onLoaderReset called");
    }

    static class MyAsyncTaskLoader extends AsyncTaskLoader<JSONObject> {
        private final MovieDetailAdapter mAdapter;

        MyAsyncTaskLoader(MovieDetailAdapter adapter) {
            super(adapter.getMovieDetailActivity());
            mAdapter = adapter;
        }

        @Override
        protected void onStartLoading() {
            Log.d(TAG, "onStartLoading called");
            mAdapter.getMovieDetailActivity().showProgressBar();
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            Log.d(TAG, "loadInBackground called");
            JSONObject json;
            URL url = null;
            try {
                url = NetworkUtils.buildMovieDetailsURL(mAdapter.getMovieID());
                if (NetworkUtils.isOnline(mAdapter.getMovieDetailActivity())) {
                    String jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                    json = new JSONObject(jsonString);
                } else {
                    Log.d(TAG, "is not online");
                    json = null;
                }
            } catch (Throwable e) {
                Log.e(TAG, "while loading " + url, e);
                json = null;
            }
            return json;
        }

        @Override
        public void deliverResult(JSONObject json) {
            Log.d(TAG, "deliverResult called");
            super.deliverResult(json);
        }
    }

}
