package com.example.android.popularmovies.data;

import org.json.JSONObject;

public class MovieListResult {

    private static final String POSTER_PATH_PREFIX = "http://image.tmdb.org/t/p/w185";

    public String poster_path;
    public String original_title;
    public String overview;
    public double vote_average;
    public double popularity;
    public int vote_count;
    public String release_date;

    private final JSONObject mJSONObject;

    public MovieListResult(JSONObject jsonObject) {
        mJSONObject = jsonObject;
    }

    public String getFullPosterPath() {
        return POSTER_PATH_PREFIX+poster_path;
    }

    public String toString() {
        return mJSONObject.toString();
    }

}
