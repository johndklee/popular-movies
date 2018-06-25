package com.example.android.popularmovies;

import android.content.res.Resources;
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
import android.widget.ImageView;

import com.example.android.popularmovies.data.AppDatabase;
import com.example.android.popularmovies.data.MovieListItemEntry;

import com.example.android.popularmovies.data.MovieListType;
import com.example.android.popularmovies.utils.JSONUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapterViewHolder>
        implements LoaderManager.LoaderCallbacks<List<MovieListItemEntry>> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private final MoviesAdapterUI mUI;
    private final RecyclerView mRecyclerView;
    private final MoviesAdapterOnClickHandler mClickHandler;

    private List<MovieListItemEntry> mMovieListItems;

    // loader id
    private static final int ID_MOVIES_LOADER = 44;

    private String mListType;
    private int mPage;
    private int mTotalPages;

    /**
     * Creates a MovieAdapter.
     *
     * @param ui           Used to talk to the UI and app resources
     * @param view         Used to get the target image size for poster
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public MoviesAdapter(@NonNull MoviesAdapterUI ui,
                         @NonNull RecyclerView view,
                         @NonNull MoviesAdapterOnClickHandler clickHandler) {
        mUI = ui;
        mRecyclerView = view;

        // detecting top most scroll so that we can trigger refresh
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    GridLayoutManager lm = (GridLayoutManager)recyclerView.getLayoutManager();
                    if (lm.findFirstCompletelyVisibleItemPosition() == 0) {
                        // top reached
                        Log.d(TAG, "top reached");
                        setListType(mListType);
                    }
                }
            }
        });

        mClickHandler = clickHandler;
        mListType = MovieListType.SORT_BY_POPULARITY;
    }

    public void setListType(String list_type) {
        mListType = list_type;
        clear();
        loadNextPage();
    }

    public String getListType() {
        return mListType;
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage called");

        if (!NetworkUtils.isOnline(getUI().getContext())) {
            // not online
            Log.d(TAG, "not online");
            return;
        }

        if (mPage <= 0) {
            // loading the very first page
            Log.d(TAG, "loading first page");
            clear();
        } else if (mTotalPages <= mPage) {
            // no more pages to load
            Log.d(TAG, "ignoring - all pages already loaded");
            return;
        }

        // load next page
        LoaderManager lm = getUI().getSupportLoaderManager();
        Loader l = lm.getLoader(ID_MOVIES_LOADER);
        if (l == null) {
            mPage++;
            Log.d(TAG, "loading " + mPage + " of " + mTotalPages);
            lm.initLoader(ID_MOVIES_LOADER, null, this);
        } else if (!lm.hasRunningLoaders()){
            mPage++;
            Log.d(TAG, "loading " + mPage + " of " + mTotalPages);
            lm.restartLoader(ID_MOVIES_LOADER, null, this);
        } else {
            Log.d(TAG, "wait for loader to finish");
        }
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getUI().getContext()).inflate(R.layout.movie_list_item, parent, false);
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
            MovieListItemEntry result = mMovieListItems.get(position);
            if (result != null) {
                image_path = result.getFullPosterPath();
            }
        } catch (Throwable e) {
            Log.d(TAG, "failed to get image path", e);
        }
        if (!TextUtils.isEmpty(image_path)) {
            Resources resources = getUI().getContext().getResources();
            int targetWidth = mRecyclerView.getWidth() / resources.getInteger(R.integer.movies_list_span_count);
            int targetHeight = targetWidth * resources.getInteger(R.integer.movie_poster_image_height)
                    / resources.getInteger(R.integer.movie_poster_image_width);
            ImageView movie_image_view = holder.itemView.findViewById(R.id.movie_image_view);
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .resize(targetWidth, targetHeight)
                    .into(movie_image_view);
        }
    }

    public List<MovieListItemEntry> getMovieListItems() {
        return this.mMovieListItems;
    }

    public void setMovieListItems(List<MovieListItemEntry> items) {
        this.mMovieListItems = items;
    }

    public MovieListItemEntry getMovieListItemAtPosition(int position) {
        MovieListItemEntry result = null;
        if (mMovieListItems != null) {
            result = mMovieListItems.get(position);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        int item_count = 0;
        if (mMovieListItems != null) {
            item_count = mMovieListItems.size();
        }
        return item_count;
    }

    private void clear() {
        if (mMovieListItems != null) {
            mMovieListItems.clear();
        }
        mPage = 0; // 0 pages loaded so far
        mTotalPages = 1; // loadNextPage will attempt to run once at least to get the real total page count
    }

    @SuppressWarnings("SameParameterValue")
    private void setPageID(int page) {
        mPage = page;
    }

    private int getPageID() {
        return mPage;
    }

    @NonNull
    @Override
    public Loader<List<MovieListItemEntry>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader called");
        return new MyAsyncTaskLoader(this);
    }

    private MoviesAdapterUI getUI() {
        return mUI;
    }

    static class MyAsyncTaskLoader extends AsyncTaskLoader<List<MovieListItemEntry>> {
        private final MoviesAdapter mAdapter;

        MyAsyncTaskLoader(MoviesAdapter adapter) {
            super(adapter.getUI().getContext());
            mAdapter = adapter;
        }

        @Override
        protected void onStartLoading() {
            Log.d(TAG, "onStartLoading called");
            mAdapter.getUI().showProgressBar();
            forceLoad();
        }

        @Nullable
        @Override
        public List<MovieListItemEntry> loadInBackground() {
            Log.d(TAG, "loadInBackground called");
            List<MovieListItemEntry> list;
            URL url = null;
            int page;
            JSONObject json;
            try {
                if (mAdapter.getPageID() <= 0) {
                    mAdapter.setPageID(1);
                }
                switch (mAdapter.getListType()) {
                    case MovieListType.SHOW_MY_FAVORITES:
                        AppDatabase db = AppDatabase.getInstance(getContext());
                        mAdapter.mPage = 1;
                        mAdapter.mTotalPages = 1;
                        list = db.movieListItemDao().loadMovieListItemsByMovieListType(MovieListType.SHOW_MY_FAVORITES);
                        break;
                    case MovieListType.SORT_BY_POPULARITY:
                    default:
                        url = NetworkUtils.buildPopularMoviesURL(mAdapter.getPageID());
                        json = new JSONObject(NetworkUtils.getResponseFromHttpUrl(url));
                        page = json.optInt("page");
                        if (page != mAdapter.mPage) {
                            throw new Throwable("got page " + page + " when expected " + mAdapter.mPage);
                        }
                        mAdapter.mTotalPages = json.optInt("total_pages");
                        list = createMovieListItemList(json);
                        break;
                    case MovieListType.SORT_BY_RATING:
                        url = NetworkUtils.buildTopRatedMoviesURL(mAdapter.getPageID());
                        json = new JSONObject(NetworkUtils.getResponseFromHttpUrl(url));
                        page = json.optInt("page");
                        if (page != mAdapter.mPage) {
                            throw new Throwable("got page " + page + " when expected " + mAdapter.mPage);
                        }
                        mAdapter.mTotalPages = json.optInt("total_pages");
                        list = createMovieListItemList(json);
                        break;
                }
            } catch (Throwable e) {
                Log.e(TAG, "while loading " + url, e);
                list = null;
            }
            return list;
        }

        private List<MovieListItemEntry> createMovieListItemList(JSONObject json) {
            ArrayList<MovieListItemEntry> list = new ArrayList<>();
            JSONArray results = json.optJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.optJSONObject(i);
                MovieListItemEntry result = JSONUtils.createMovieListResult(jsonObject, mAdapter.getUI().getContext());
                list.add(result);
            }
            return list;
        }

    }


    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieListItemEntry>> loader, List<MovieListItemEntry> list) {
        Log.d(TAG, "onLoadFinished called");
        getUI().hideProgressBar();
        try {
            if (MovieListType.SHOW_MY_FAVORITES.equals(mListType) || mMovieListItems == null) {
                mMovieListItems = list;
            } else {
                mMovieListItems.addAll(list);
            }
        } catch (Throwable e) {
            Log.e(TAG, "failed to load data", e);
        }
        if (list == null) {
            getUI().showErrorDisplay();
        } else {
            getUI().showResult();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieListItemEntry>> loader) {
        Log.d(TAG, "onLoaderReset called");
    }

}
