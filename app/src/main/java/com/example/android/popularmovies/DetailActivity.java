package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.popularmovies.data.AppDatabase;
import com.example.android.popularmovies.data.MovieListItemEntry;
import com.example.android.popularmovies.data.MovieListType;
import com.example.android.popularmovies.data.MovieReviewEntry;
import com.example.android.popularmovies.data.MovieVideoEntry;
import com.example.android.popularmovies.utils.VideoUtils;

import java.util.List;

public class DetailActivity extends AppCompatActivity
        implements MovieDetailHighlightFragment.OnFragmentInteractionListener,
            MovieVideosAdapterOnClickHandler, MovieReviewsAdapterOnClickHandler,
            MovieDetailAdapterUI {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private ProgressBar mProgressBar;
    private TextView mErrorDisplay;
    private ScrollView mMovieDetailView;
    private MovieDetailAdapter mMovieDetailAdapter;
    private MovieVideosAdapter mMovieVideosAdapter;
    private MovieReviewsAdapter mMovieReviewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mErrorDisplay = findViewById(R.id.tv_error_display);
        mMovieDetailView = findViewById(R.id.movie_detail_view);

        RecyclerView mMovieVideos = findViewById(R.id.movie_videos);
        RecyclerView mMovieReviews = findViewById(R.id.movie_reviews);

        // to avoid scrolling down to give focus to one of RecyclerView
        // instead of displaying at the top of ScrollView and display movie title header
        // https://stackoverflow.com/questions/16886077/android-scrollview-doesnt-start-at-top-but-at-the-beginning-of-the-gridview
        mMovieVideos.setFocusable(false);
        mMovieReviews.setFocusable(false);

        // don't want to scroll RecyclerView individually and use outer ScrollView only
        LinearLayoutManager lmv = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieVideos.setLayoutManager(lmv);
        mMovieVideos.setHasFixedSize(true);
        mMovieVideos.setNestedScrollingEnabled(true);
        LinearLayoutManager lmr = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieReviews.setLayoutManager(lmr);
        mMovieReviews.setHasFixedSize(false);
        mMovieReviews.setNestedScrollingEnabled(true);

        // https://stackoverflow.com/questions/10316743/detect-end-of-scrollview
        // how to detect scrollview reached bottom
        mMovieDetailView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (mMovieDetailView != null) {
                    if (mMovieDetailView.getChildAt(0).getBottom() <= (mMovieDetailView.getHeight() + mMovieDetailView.getScrollY())) {
                        //scroll view is at bottom
                        Log.d(TAG, "reached bottom");
                        mMovieReviewsAdapter.loadNextPage();
                    }
                }
            }
        });

        try {

            String movie_id = getIntent().getStringExtra("movie_id");
            Log.d(TAG, "movie_id="+movie_id);

            mMovieVideosAdapter = new MovieVideosAdapter(this, mMovieVideos, this);
            mMovieVideos.setAdapter(mMovieVideosAdapter);
            mMovieVideosAdapter.setMovieID(movie_id);

            mMovieReviewsAdapter = new MovieReviewsAdapter(this, mMovieReviews, this);
            mMovieReviews.setAdapter(mMovieReviewsAdapter);
            mMovieReviewsAdapter.setMovieID(movie_id);

            mMovieDetailAdapter = new MovieDetailAdapter(this);
            mMovieDetailAdapter.loadMovieDetail(movie_id);

            ToggleButton my_favorite_toggle_button = findViewById(R.id.my_favorite_toggle_button);
            my_favorite_toggle_button.setChecked(false);
            loadFavoriteStatus(movie_id);
            my_favorite_toggle_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MovieListItemEntry result = DetailActivity.this.mMovieDetailAdapter.getMovieDetailEntry();
                    favoriteStatusChanged(result, isChecked);
                }
            });

        } catch (Throwable e) {
            Log.d(TAG, "onCreate while digesting string extra from intent", e);
        }

    }

    private void favoriteStatusChanged(final MovieListItemEntry result, final boolean isChecked) {
        Log.d(TAG, "toggle button is checked = "+isChecked);
        if (result != null) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getInstance(DetailActivity.this);
                    Log.d(TAG, "deleting "+result);
                    db.movieListItemDao().deleteMovieListItem(result);
                    if (isChecked) {
                        Log.d(TAG, "inserting " + result);
                        db.movieListItemDao().insertMovieListItem(result);
                    }
                }
            });
        }
    }

    private void loadFavoriteStatus(final String movie_id) {
        Log.d(TAG, "load favorite status");
        if (!TextUtils.isEmpty(movie_id)) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    AppDatabase db = AppDatabase.getInstance(DetailActivity.this);
                    Log.d(TAG, "get "+movie_id);
                    List<MovieListItemEntry> result = db.movieListItemDao().findMovieItemByPKs(MovieListType.SHOW_MY_FAVORITES, movie_id);
                    final boolean isChecked = (result != null && !result.isEmpty());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToggleButton my_favorite_toggle_button = findViewById(R.id.my_favorite_toggle_button);
                            my_favorite_toggle_button.setChecked(isChecked);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void showErrorDisplay() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.VISIBLE);
        mErrorDisplay.setText(R.string.api_error);
    }

    @Override
    public void hideErrorDisplay() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public LoaderManager getSupportLoaderManager() {
        return super.getSupportLoaderManager();
    }

    public void setVideoCount(int video_counts) {
        TextView movie_videos_summary = findViewById(R.id.movie_videos_summary);
        String text = getResources().getQuantityString(R.plurals.movie_videos_summary, video_counts, video_counts);
        movie_videos_summary.setText(text);
    }

    public void setReviewCount(int review_counts) {
        TextView movie_reviews_summary = findViewById(R.id.movie_reviews_summary);
        String text = getResources().getQuantityString(R.plurals.movie_reviews_summary, review_counts, review_counts);
        movie_reviews_summary.setText(text);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction called with uri="+uri);
    }

    @Override
    public void onVideoClick(int position) {
        Log.d(TAG, "onVideoClick called with position="+position);
        MovieVideoEntry result = mMovieVideosAdapter.getMovieVideosResult(position);
        if (result != null) {
            String video_site = result.getVideoSite();
            String video_key = result.getVideoKey();
            if ("youtube".equalsIgnoreCase(video_site) && !TextUtils.isEmpty(video_key)) {
                Log.d(TAG, "launching youtube "+video_key);
                VideoUtils.watchYoutubeVideo(this, video_key);
            }
        }
    }

    @Override
    public void onReviewClick(int position) {
        Log.d(TAG, "onReviewClick called with position="+position);
        MovieReviewEntry result = mMovieReviewsAdapter.getMovieReviewsResult(position);
        if (result != null) {
            String review_url = result.getReviewURL();
            if (!TextUtils.isEmpty(review_url)) {
                Log.d(TAG, "maybe can launch "+review_url);
            }
        }
    }

}
