package com.example.android.popularmovies.utils;

import android.content.Context;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieListResult;

import org.json.JSONObject;

public class JSONUtils {

    public static MovieListResult createMovieListResult(JSONObject jsonObject, Context context) {
        MovieListResult result = new MovieListResult(jsonObject);

//        result.id = jsonObject.optInt("id");
        result.poster_path = jsonObject.optString("poster_path");

        String no_data = context.getResources().getString(R.string.no_data_message);

        //        result.title = jsonObject.optString("title", no_data);
        result.original_title = jsonObject.optString("original_title", no_data);
        result.overview = jsonObject.optString("overview", no_data);
        result.release_date = jsonObject.optString("release_date", no_data);

        result.vote_count = jsonObject.optInt("vote_count");
        result.vote_average = jsonObject.optDouble("vote_average");
        result.popularity = jsonObject.optDouble("popularity");

        return result;
    }
}
