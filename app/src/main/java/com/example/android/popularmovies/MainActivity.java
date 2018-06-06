package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieListResult;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SORT_BY_RATING = "sort_by_rating";

    private ProgressBar mProgressBar;
    private TextView mErrorDisplay;
    private RecyclerView mMoviesList;
    private MoviesAdapter mMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mErrorDisplay = findViewById(R.id.tv_error_display);

        mMoviesList = findViewById(R.id.movies_list);
        mMoviesList.setHasFixedSize(true);
        int mSpanCount = getResources().getInteger(R.integer.movies_list_span_count);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, mSpanCount, GridLayoutManager.VERTICAL, false);
        mMoviesList.setLayoutManager(layoutManager);
        mMoviesAdapter = new MoviesAdapter(this, mMoviesList, this);
        mMoviesList.setAdapter(mMoviesAdapter);

        // retrieve sort by setting from saved instance state if available
        boolean sort_by_rating = false;
        if (savedInstanceState != null) {
            sort_by_rating = savedInstanceState.getBoolean(SORT_BY_RATING, false);
        }
        if (sort_by_rating) {
            mMoviesAdapter.loadSortByRating();
        } else {
            mMoviesAdapter.loadSortByPopularity();
        }
    }

    /**
     * save instance state - for now, sort by setting is the only state being saved
     * @param outState Bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SORT_BY_RATING, mMoviesAdapter.isSortByRating());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(int position) {
        Log.d(TAG, "clicked position="+position);
        MovieListResult result = mMoviesAdapter.getMovieListResult(position);
        if (result != null) {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            // DONE: pass along data needed for Movie details UI
            intent.putExtra("json", result.toString());
            startActivity(intent);
        } else {
            Log.d(TAG, "cannot load movie detail info");
        }
    }

    public void showErrorDisplay() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.VISIBLE);
        mErrorDisplay.setText(R.string.api_error);
    }

    public void hideErrorDisplay() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
    }

    public void showProgressBar() {
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        mErrorDisplay.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.menu_item_sort_by_popularity:
                mMoviesAdapter.loadSortByPopularity();
                return true;
            case R.id.menu_item_sort_by_rating:
                mMoviesAdapter.loadSortByRating();
                return true;
            case R.id.menu_item_show_my_favorites:
                mMoviesAdapter.loadMyFavorites();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
