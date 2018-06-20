package com.example.android.popularmovies.data;

import org.json.JSONObject;

public class MovieDetailsResult extends MovieListResult {

    public int runtime;

    public int getMovieLengthInMinutes() {
        return runtime;
    }

    public MovieDetailsResult(JSONObject jsonObject) {
        super(jsonObject);
    }

}
