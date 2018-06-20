package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.data.MovieListResult;

import com.example.android.popularmovies.utils.JSONUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapterViewHolder>
        implements LoaderManager.LoaderCallbacks<JSONObject> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private final MainActivity mActivity;
    private final RecyclerView mMoviesList;
    private final MovieAdapterOnClickHandler mClickHandler;
    private final ArrayList<MovieListResult> mMovieListResults;

    private static final int ID_MOVIES_LOADER = 44;

    private static final int SORT_BY_RATING = 3;
    private static final int SORT_BY_POPULARITY = 4;
    private static final int SHOW_MY_FAVORITES = 5;

    private int mSort;
    private int mPage;
    private int mTotalPages;

    /**
     * Creates a MovieAdapter.
     *
     * @param activity     Used to talk to the UI and app resources
     * @param view         Used to get the target image size for poster
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MoviesAdapter(@NonNull MainActivity activity, @NonNull RecyclerView view,
                         @NonNull MovieAdapterOnClickHandler clickHandler) {
        mActivity = activity;
        mMoviesList = view;
        // detecting top most scroll so that we can trigger refresh
        mMoviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    GridLayoutManager lm = (GridLayoutManager)recyclerView.getLayoutManager();
                    if (lm.findFirstCompletelyVisibleItemPosition() == 0) {
                        // top reached
                        Log.d(TAG, "top reached");
                        if (mSort == SORT_BY_RATING) {
                            loadSortByRating();
                        } else {
                            loadSortByPopularity();
                        }
                    }
                }
            }
        });

        mClickHandler = clickHandler;
        mMovieListResults = new ArrayList<>();
        mSort = SORT_BY_POPULARITY;
    }

    public void loadSortByPopularity() {
        Log.d(TAG, "loadSortByPopularity called");
        clear();
        mSort = SORT_BY_POPULARITY;
        loadNextPage();
    }

    public void loadSortByRating() {
        Log.d(TAG, "loadSortByRating called");
        clear();
        mSort = SORT_BY_RATING;
        loadNextPage();
    }

    public void loadMyFavorites() {
        Log.d(TAG, "loadMyFavorites called");
        clear();
        mSort = SHOW_MY_FAVORITES;
        loadNextPage();
    }

    public boolean isSortByRating() {
        return (mSort == SORT_BY_RATING);
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage called");

        if (mPage <= 0) {
            // loading the very first page
            Log.d(TAG, "loading first page");
            setPageID(1);
            mTotalPages = 0;
            clear();
        } else if (mTotalPages <= mPage) {
            // no more pages to load
            Log.d(TAG, "ignoring as all pages loaded");
            return;
        } else {
            // load next page
            setPageID(mPage+1);
            Log.d(TAG, "loading " + mPage + " of " + mTotalPages);
        }

        LoaderManager lm = getMainActivity().getSupportLoaderManager();
        Loader l = lm.getLoader(ID_MOVIES_LOADER);
        if (l == null) {
            lm.initLoader(ID_MOVIES_LOADER, null, this);
        } else if (!lm.hasRunningLoaders()){
            lm.restartLoader(ID_MOVIES_LOADER, null, this);
        } else {
            Log.d(TAG, "wait for loader to finish");
        }

    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getMainActivity()).inflate(R.layout.movie_list_item, parent, false);
        view.setFocusable(true);
        return new MoviesAdapterViewHolder(view, mClickHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for "+position);
        String image_path = null;
        try {
            if (position >= (getItemCount()-1)) {
                // loading the last item, so load the next page
                loadNextPage();
            }
            MovieListResult result = mMovieListResults.get(position);
            if (result != null) {
                image_path = result.getFullPosterPath();
            }
        } catch (Throwable e) {
            Log.d(TAG, "failed to get image path", e);
        }
        if (!TextUtils.isEmpty(image_path)) {
            int targetWidth = mMoviesList.getWidth() / getMainActivity().getResources().getInteger(R.integer.movies_list_span_count);
            int targetHeight = targetWidth * getMainActivity().getResources().getInteger(R.integer.movie_poster_image_height)
                    / getMainActivity().getResources().getInteger(R.integer.movie_poster_image_width);
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .resize(targetWidth, targetHeight)
                    .into(holder.getImageView());
        }
    }

    public MovieListResult getMovieListResult(int position) {
        return mMovieListResults.get(position);
    }

    @Override
    public int getItemCount() {
        return mMovieListResults.size();
    }

    private void clear() {
        mMovieListResults.clear();
        setPageID(-1);
        mTotalPages = -1;
    }

    private void add(MovieListResult result) {
        mMovieListResults.add(result);
    }

    private void setPageID(int page) {
        mPage = page;
    }

    private int getPageID() {
        return mPage;
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader called");
        return new MyAsyncTaskLoader(this);
    }

    private MainActivity getMainActivity() {
        return mActivity;
    }

    static class MyAsyncTaskLoader extends AsyncTaskLoader<JSONObject> {
        private final MoviesAdapter mAdapter;

        MyAsyncTaskLoader(MoviesAdapter adapter) {
            super(adapter.getMainActivity());
            mAdapter = adapter;
        }

        @Override
        protected void onStartLoading() {
            Log.d(TAG, "onStartLoading called");
            mAdapter.getMainActivity().showProgressBar();
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            Log.d(TAG, "loadInBackground called");
            JSONObject json;
            URL url = null;
            if (mAdapter.getPageID() <= 0) {
                mAdapter.setPageID(1);
            }
            try {
                if (mAdapter.isSortByRating()) {
                    url = NetworkUtils.buildTopRatedMoviesURL(mAdapter.getPageID());
                } else {
                    url = NetworkUtils.buildPopularMoviesURL(mAdapter.getPageID());
                }
                if (NetworkUtils.isOnline(mAdapter.getMainActivity())) {
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
        getMainActivity().hideProgressBar();
        try {
            if (json == null) {
                 throw new Throwable("json object is null");
            }
            int page = json.optInt("page");
            if (page != mPage) {
                throw new Throwable("got page " + page + " when expected " + mPage);
            }
            int total_results = json.optInt("total_results");
            Log.d(TAG, "total_results = " + total_results);
            mTotalPages = json.optInt("total_pages");
            JSONArray results = json.optJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.optJSONObject(i);
                MovieListResult result = JSONUtils.createMovieListResult(jsonObject, getMainActivity());
                add(result);
            }
        } catch (Throwable e) {
            Log.e(TAG, "failed to load data", e);
        }
        if (json == null) {
            getMainActivity().showErrorDisplay();
        } else {
            getMainActivity().hideErrorDisplay();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {
        Log.d(TAG, "onLoaderReset called");
    }

}
