package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieVideosResult;
import com.example.android.popularmovies.utils.JSONUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapterViewHolder>
        implements LoaderManager.LoaderCallbacks<JSONObject> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private final MovieDetailActivity mActivity;
    private final MovieVideosAdapterOnClickHandler mClickHandler;
    private final ArrayList<MovieVideosResult> mMovieVideos;

    private static final int ID_MOVIE_VIDEOS_LOADER = 46;

    private String mMovieID;

    /**
     * Creates a MovieAdapter.
     *
     * @param activity     Used to talk to the UI and app resources
     * @param view         Used to get the target image size for poster
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    MovieVideosAdapter(@NonNull MovieDetailActivity activity, @NonNull RecyclerView view,
                         @NonNull MovieVideosAdapterOnClickHandler clickHandler) {
        mActivity = activity;
        mClickHandler = clickHandler;
        mMovieVideos = new ArrayList<>();
    }

    private void loadMovieVideos() {
        Log.d(TAG, "loadMovieVideos called");

        clear();

        LoaderManager lm = getMovieDetailActivity().getSupportLoaderManager();
        Loader l = lm.getLoader(ID_MOVIE_VIDEOS_LOADER);
        if (l == null) {
            lm.initLoader(ID_MOVIE_VIDEOS_LOADER, null, this);
        } else if (!lm.hasRunningLoaders()){
            lm.restartLoader(ID_MOVIE_VIDEOS_LOADER, null, this);
        } else {
            Log.d(TAG, "wait for loader to finish");
        }

    }

    @NonNull
    @Override
    public MovieVideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getMovieDetailActivity()).inflate(R.layout.movie_video_line_item, parent, false);
        view.setFocusable(true);
        return new MovieVideosAdapterViewHolder(view, mClickHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideosAdapterViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for "+position);
        try {
            MovieVideosResult result = mMovieVideos.get(position);
            if (result != null) {
                TextView video_title = holder.itemView.findViewById(R.id.video_title);
                video_title.setText(result.getVideoName());
            }
        } catch (Throwable e) {
            Log.d(TAG, "failed to get video result", e);
        }
    }

    public MovieVideosResult getMovieVideosResult(int position) {
        return mMovieVideos.get(position);
    }

    @Override
    public int getItemCount() {
        return mMovieVideos.size();
    }

    private void clear() {
        mMovieVideos.clear();
    }

    private void add(MovieVideosResult result) {
        mMovieVideos.add(result);
    }

    private String getMovieID() {
        return mMovieID;
    }

    public void setMovieID(String movie_id) {
        mMovieID = movie_id;
        loadMovieVideos();
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader called");
        return new MyAsyncTaskLoader(this);
    }

    private MovieDetailActivity getMovieDetailActivity() {
        return mActivity;
    }

    static class MyAsyncTaskLoader extends AsyncTaskLoader<JSONObject> {
        private final MovieVideosAdapter mAdapter;

        MyAsyncTaskLoader(MovieVideosAdapter adapter) {
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
                url = NetworkUtils.buildMovieVideosURL(mAdapter.getMovieID());
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

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject json) {
        Log.d(TAG, "onLoadFinished called");
        getMovieDetailActivity().hideProgressBar();
        try {
            if (json == null) {
                throw new Throwable("json object is null");
            }
            Log.d(TAG, "json = "+json);
            JSONArray results = json.optJSONArray("results");
            mActivity.setVideoCount(results.length());
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.optJSONObject(i);
                MovieVideosResult result = JSONUtils.createMovieVideosResult(jsonObject, getMovieDetailActivity());
                add(result);
            }
        } catch (Throwable e) {
            Log.e(TAG, "failed to load data", e);
        }
        if (json == null) {
            getMovieDetailActivity().showErrorDisplay();
        } else {
            getMovieDetailActivity().hideErrorDisplay();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {
        Log.d(TAG, "onLoaderReset called");
    }

}
