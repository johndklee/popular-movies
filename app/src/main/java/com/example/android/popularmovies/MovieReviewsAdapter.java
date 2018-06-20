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

import com.example.android.popularmovies.data.MovieReviewsResult;
import com.example.android.popularmovies.utils.JSONUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapterViewHolder>
        implements LoaderManager.LoaderCallbacks<JSONObject> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private final MovieDetailActivity mActivity;
    private final MovieReviewsAdapterOnClickHandler mClickHandler;
    private final ArrayList<MovieReviewsResult> mMovieReviews;

    private static final int ID_MOVIE_REVIEWS_LOADER = 47;

    private String mMovieID;

    private int mPageID;
    private int mMaxPageID;

    /**
     * Creates a MovieReviewsAdapter.
     *
     * @param activity     Used to talk to the UI and app resources
     * @param view         Used to get the target image size for poster
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MovieReviewsAdapter(@NonNull MovieDetailActivity activity, @NonNull RecyclerView view,
                              @NonNull MovieReviewsAdapterOnClickHandler clickHandler) {
        mActivity = activity;
        mClickHandler = clickHandler;
        mMovieReviews = new ArrayList<>();
        setMovieID(null);
    }

    public synchronized void loadNextPage() {
        Log.d(TAG, "loadNextPage called");

        if (getPageID() < getMaxPageID()) {
            setPageID(getPageID()+1);
            LoaderManager lm = getMovieDetailActivity().getSupportLoaderManager();
            Loader l = lm.getLoader(ID_MOVIE_REVIEWS_LOADER);
            if (l == null) {
                lm.initLoader(ID_MOVIE_REVIEWS_LOADER, null, this);
            } else if (!lm.hasRunningLoaders()) {
                lm.restartLoader(ID_MOVIE_REVIEWS_LOADER, null, this);
            } else {
                Log.d(TAG, "wait for loader to finish");
            }
        } else {
            Log.d(TAG, "already loaded the last page");
        }
    }

    private void setPageID(int page_id) {
        Log.d(TAG, "setPageID("+page_id+") called");
        mPageID = page_id;
    }

    private int getPageID() {
        return mPageID;
    }

    private void setMaxPageID(int total_pages) {
        Log.d(TAG, "setMaxPageID("+total_pages+") called");
        mMaxPageID = total_pages;
    }

    private int getMaxPageID() {
        return mMaxPageID;
    }

    @NonNull
    @Override
    public MovieReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getMovieDetailActivity()).inflate(R.layout.movie_review_line_item, parent, false);
        view.setFocusable(true);
        return new MovieReviewsAdapterViewHolder(view, mClickHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsAdapterViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for "+position);
        String image_path = null;
        try {
            MovieReviewsResult result = mMovieReviews.get(position);
            if (result != null) {
                TextView review_author = holder.itemView.findViewById(R.id.review_author);
                review_author.setText(result.review_author);
                TextView review_content = holder.itemView.findViewById(R.id.review_content);
                review_content.setText(result.review_content);
            }
        } catch (Throwable e) {
            Log.d(TAG, "failed to get review result", e);
        }
    }

    public MovieReviewsResult getMovieReviewsResult(int position) {
        return mMovieReviews.get(position);
    }

    @Override
    public int getItemCount() {
        return mMovieReviews.size();
    }

    private void clear() {
        mMovieReviews.clear();
    }

    private void add(MovieReviewsResult result) {
        mMovieReviews.add(result);
    }

    private String getMovieID() {
        return mMovieID;
    }

    public void setMovieID(String movie_id) {
        mMovieID = movie_id;
        mPageID = 0;
        mMaxPageID = 1;
        if (mMovieID != null) {
            loadNextPage();
        }
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
        private final MovieReviewsAdapter mAdapter;

        MyAsyncTaskLoader(MovieReviewsAdapter adapter) {
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
                url = NetworkUtils.buildMovieReviewsURL(mAdapter.getMovieID(), mAdapter.getPageID());
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
            setMaxPageID(json.getInt("total_pages"));
            mActivity.setReviewCount(json.getInt("total_results"));
            JSONArray results = json.optJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.optJSONObject(i);
                MovieReviewsResult result = JSONUtils.createMovieReviewsResult(jsonObject, getMovieDetailActivity());
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
