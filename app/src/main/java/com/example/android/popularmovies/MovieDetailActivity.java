package com.example.android.popularmovies;

import android.net.Uri;
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

import com.example.android.popularmovies.data.MovieReviewsResult;
import com.example.android.popularmovies.data.MovieVideosResult;
import com.example.android.popularmovies.utils.VideoUtils;

public class MovieDetailActivity extends AppCompatActivity
        implements MovieDetailHighlightFragment.OnFragmentInteractionListener,
            MovieVideosAdapterOnClickHandler, MovieReviewsAdapterOnClickHandler {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private ProgressBar mProgressBar;
    private TextView mErrorDisplay;
    private ScrollView mMovieDetailView;
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
        LinearLayoutManager lmv = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mMovieVideos.setLayoutManager(lmv);
        mMovieVideos.setHasFixedSize(true);
        LinearLayoutManager lmr = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mMovieReviews.setLayoutManager(lmr);
        mMovieReviews.setHasFixedSize(false);

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

            MovieDetailAdapter mMovieDetailAdapter = new MovieDetailAdapter(this);
            mMovieDetailAdapter.setMovieID(movie_id);

            mMovieVideosAdapter = new MovieVideosAdapter(this, mMovieVideos, this);
            mMovieVideos.setAdapter(mMovieVideosAdapter);
            mMovieVideosAdapter.setMovieID(movie_id);

            mMovieReviewsAdapter = new MovieReviewsAdapter(this, mMovieReviews, this);
            mMovieReviews.setAdapter(mMovieReviewsAdapter);
            mMovieReviewsAdapter.setMovieID(movie_id);

            ToggleButton my_favorite_toggle_button = findViewById(R.id.my_favorite_toggle_button);
            my_favorite_toggle_button.setChecked(true);
            my_favorite_toggle_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, "toggle button is checked = "+isChecked);
                }
            });

        } catch (Throwable e) {
            Log.d(TAG, "onCreate while digesting string extra from intent", e);
        }

    }

    public void showErrorDisplay() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.VISIBLE);
        mErrorDisplay.setText(R.string.api_error);
    }

    public void hideErrorDisplay() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.VISIBLE);
    }

    public void showProgressBar() {
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieDetailView.setVisibility(View.VISIBLE);
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
        MovieVideosResult result = mMovieVideosAdapter.getMovieVideosResult(position);
        Log.d(TAG, "result=" + result);
        if (result != null) {
            String youtube_key = result.getYoutubeKey();
            if (!TextUtils.isEmpty(youtube_key)) {
                VideoUtils.watchYoutubeVideo(this, youtube_key);
            }
        }
    }

    @Override
    public void onReviewClick(int position) {
        Log.d(TAG, "onReviewClick called with position="+position);
        MovieReviewsResult result = mMovieReviewsAdapter.getMovieReviewsResult(position);
        if (result != null) {
            Log.d(TAG, "result="+result);
        }
    }

}
