package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
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

import com.example.android.popularmovies.data.MovieListItemEntry;
import com.example.android.popularmovies.data.MovieListType;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapterOnClickHandler, MoviesAdapterUI {

    private static final String TAG = MainActivity.class.getSimpleName();

    // key for saving to instance state
    private static final String MOVIE_LIST_TYPE = "movie_list_type";

    private RecyclerView mMovieList;
    private ProgressBar mProgressBar;
    private TextView mErrorDisplay;

    private MoviesAdapter mMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieList = findViewById(R.id.movie_list);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mErrorDisplay = findViewById(R.id.tv_error_display);

        // setup layout manager for recycler view
        int mSpanCount = getResources().getInteger(R.integer.movies_list_span_count);
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, mSpanCount, GridLayoutManager.VERTICAL, false);
        mMovieList.setHasFixedSize(true);
        mMovieList.setLayoutManager(layoutManager);

        // setup data adapter for recycler view
        mMoviesAdapter = new MoviesAdapter(this, mMovieList, this);
        // retrieve sort by setting from saved instance state if available
        String list_type = MovieListType.SORT_BY_POPULARITY;
        if (savedInstanceState != null) {
            list_type = savedInstanceState.getString(MOVIE_LIST_TYPE, MovieListType.SORT_BY_POPULARITY);
        }
        mMoviesAdapter.setListType(list_type);
        mMovieList.setAdapter(mMoviesAdapter);

    }

    /**
     * save instance state - for now, sort by setting is the only state being saved
     * @param outState Bundle
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(MOVIE_LIST_TYPE, mMoviesAdapter.getListType());
        super.onSaveInstanceState(outState);
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
    @SuppressWarnings("SameReturnValue")
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
    @SuppressWarnings("SameReturnValue")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id) {
            case R.id.menu_item_sort_by_popularity:
                mMoviesAdapter.setListType(MovieListType.SORT_BY_POPULARITY);
                return true;
            case R.id.menu_item_sort_by_rating:
                mMoviesAdapter.setListType(MovieListType.SORT_BY_RATING);
                return true;
            case R.id.menu_item_show_my_favorites:
                mMoviesAdapter.setListType(MovieListType.SHOW_MY_FAVORITES);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Clicked on one of movie thumbnails displayed in recycler view
     * @param position Position of thumbnail clicked
     */
    @Override
    public void onClick(int position) {
        Log.d(TAG, "clicked position="+position);
        MovieListItemEntry result = mMoviesAdapter.getMovieListItemAtPosition(position);
        if (result != null) {
            Intent intent = new Intent(this, DetailActivity.class);
            // DONE: pass along data needed for Movie details UI
            intent.putExtra("movie_id", result.getMovieID());
            startActivity(intent);
        } else {
            Log.d(TAG, "cannot load movie detail info");
        }
    }

    @Override
    public void showErrorDisplay() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mMovieList.setVisibility(View.INVISIBLE);

        mErrorDisplay.setVisibility(View.VISIBLE);
        mErrorDisplay.setText(R.string.api_error);
    }

    @Override
    public void showResult() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorDisplay.setVisibility(View.INVISIBLE);

        mMovieList.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public LoaderManager getSupportLoaderManager() {
        return super.getSupportLoaderManager();
    }

}
