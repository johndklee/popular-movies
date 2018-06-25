package com.example.android.popularmovies.data;

public class MovieDetailEntry extends MovieListItemEntry {

    private static final String TAG = MovieDetailEntry.class.getSimpleName();

    private int runtime;

    public MovieDetailEntry() {
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String toString() {
        return TAG+":"+getMovieID();
    }

}
